package com.shopping.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.shopping.coupon.constant.Constant;
import com.shopping.coupon.constant.CouponStatus;
import com.shopping.coupon.dao.CouponDao;
import com.shopping.coupon.entity.Coupon;
import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.feign.SettlementClient;
import com.shopping.coupon.feign.TemplateClient;
import com.shopping.coupon.service.IRedisService;
import com.shopping.coupon.service.IUserService;
import com.shopping.coupon.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of User Service
 * All operations and states are saved to Redis, and send message to MySQL via Kafka
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private CouponDao couponDao;

    private IRedisService redisService;

    // template microservice client
    private TemplateClient templateClient;

    // settlement microservice client
    private SettlementClient settlementClient;

    // <topic type, message type> kafka client
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public UserServiceImpl(CouponDao couponDao, IRedisService redisService, TemplateClient templateClient,
                           SettlementClient settlementClient, KafkaTemplate<String, String> kafkaTemplate) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public List<Coupon> findUserOwnCouponByStatus(Long userId, Integer status) throws CouponException {
        // Try to get a user coupons info from cache
        List<Coupon> cacheCoupons = redisService.getCachedCoupons(userId, status);
        List<Coupon> response;

        if (CollectionUtils.isNotEmpty(cacheCoupons)) {
            log.debug("Coupon Cache is Not Empty: {} {}", userId, status);
            response = cacheCoupons;
        } else {
            log.debug("Coupon Cache is Empty, get Coupon From DB: {}, {}", userId, status);
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(userId, CouponStatus.of(status));

            if (CollectionUtils.isNotEmpty(dbCoupons)) {
                log.debug("Current user doesn't have any coupon: {} {}", userId, status);
                return dbCoupons;
            }

            // add templateSDK to dbCoupons, because this properties doesn't exist in database
            // use template microservice
            Map<Integer, CouponTemplateSDK> id2TemplateSDK = templateClient.findIds2TemplateSDK(dbCoupons.stream()
                    .map(Coupon::getTemplateId)
                    .collect(Collectors.toList())).getData();

            dbCoupons.forEach(
                    dbCoupon -> dbCoupon.setTemplateSDK(id2TemplateSDK.get(dbCoupon.getTemplateId())));

            // dbCoupon with template SDK
            response = dbCoupons;

            //update cache, write the data to redis;
            redisService.addCouponToCache(userId, response, status);
        }

        // remove invalid coupons
        response = response.stream()
                .filter(coupon -> coupon.getId() != -1)
                .collect(Collectors.toList());

        // if current coupon is available coupon, need to check/process the expired coupon due to delay expire strategy
        if (CouponStatus.of(status) == CouponStatus.AVAILABLE) {
            CouponClassify classify = CouponClassify.classify(response);
            // expire strategy
            if (CollectionUtils.isNotEmpty(classify.getExpired())) {
                log.info("Add Expired Coupon To Cache From FindCouponByStatus: {}, {}", userId, status);
                redisService.addCouponToCache(userId, classify.getExpired(), CouponStatus.EXPIRED.getCode());

                // send kafka message to async save to db
                kafkaTemplate.send(Constant.TOPIC, JSON.toJSONString(new CouponKafkaMessage(
                        CouponStatus.EXPIRED.getCode(), classify.getExpired().stream()
                        .map(Coupon::getId)
                        .collect(Collectors.toList()))));
            }
            return classify.getAvailable();
        }

        return response;
    }

    /**
     * Get user available coupon template
     *
     * @param userId user id
     * @return CouponTemplateSDK
     * @throws CouponException exception
     */
    @Override
    public List<CouponTemplateSDK> findAvailableCouponTemplate(Long userId) throws CouponException {

        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKS = templateClient.findAllAvailableTemplate().getData();
        log.debug("Find All Templates From TemplateClient Count: {}", templateSDKS.size());

        //filter expired coupon template
        templateSDKS = templateSDKS.stream()
                .filter(template -> template.getRule().getExpiration().getExpiredDate() > curTime)
                .collect(Collectors.toList());

        log.info("Find Usable Template Count: {}", templateSDKS.size());

        // key - template id
        // <template limitation(how many coupons each user can acquire), couponTemplateSDK>
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template = new HashMap<>(templateSDKS.size());
        templateSDKS.forEach(
                template -> limit2Template.put(template.getId(), Pair.of(template.getRule().getLimitation(), template))
        );

        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
        List<Coupon> userAvailableCoupons = findUserOwnCouponByStatus(userId, CouponStatus.AVAILABLE.getCode());
        log.debug("Current user available coupons: {}, {}", userId, userAvailableCoupons.size());

        // current user's coupon
        Map<Integer, List<Coupon>> templateId2Coupons = userAvailableCoupons
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));

        // check if the uses can acquire coupon template by Template rule
        limit2Template.forEach((k, v) -> {
            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();

            if (templateId2Coupons.containsKey(k) && templateId2Coupons.get(k).size() >= limitation) {
                return;
            }

            result.add(templateSDK);

        });
        return result;
    }

    /**
     * User acquires a coupon
     * 1. Get coupons from templateClient, and then check whether they are valid
     * 2. Check if use can acquire the coupon based on limitation
     * 3. Save data to db
     * 4. Fill CouponTemplateSDK
     * 5. Save to cache
     *
     * @param request coupon acquire request
     * @return coupon
     * @throws CouponException exception
     */
    @Override
    public Coupon collectCoupon(AcquireTemplateRequest request) throws CouponException {
        // Get all the coupon template user can acquire
        Map<Integer, CouponTemplateSDK> id2Template = templateClient
                .findIds2TemplateSDK(Collections.singletonList(request.getTemplateSDK().getId())).getData();

        // Check if coupon template is existed
        if (id2Template.size() <= 0) {
            log.error("Coupon Template is Not Existed, Cannot Acquire From Template Client: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Cannot Acquire From Template Client");
        }

        // Check if user can acquire the coupon (rule)
        List<Coupon> userAvailableCoupons = findUserOwnCouponByStatus(request.getUserId(), CouponStatus.AVAILABLE.getCode());
        Map<Integer, List<Coupon>> templateId2Coupons = userAvailableCoupons.stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        if (templateId2Coupons.containsKey(request.getTemplateSDK().getId()) &&
                templateId2Coupons.get((request.getTemplateSDK().getId())).size() >=
                        request.getTemplateSDK().getRule().getLimitation()) {
            log.error("Exceed Template Acquirement Limitation: {}", request.getTemplateSDK().getId());
        }
        // Try to get a coupon code
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(
                request.getTemplateSDK().getId()
        );

        // All the coupons have been collected by users
        if (StringUtils.isEmpty(couponCode)) {
            log.error("Can Not Acquire Coupon Code: {}", request.getTemplateSDK().getId());
            throw new CouponException("Cannot Acquire Coupon Code");
        }

        // without primary key, id here is template Id
        Coupon newCoupon = new Coupon(request.getTemplateSDK().getId(), request.getUserId(),
                couponCode, CouponStatus.AVAILABLE);

        // it will generate a primary key, because it is saved to DB
        newCoupon = couponDao.save(newCoupon);

        // add property couponSDK to Coupon before saving to cache
        newCoupon.setTemplateSDK(request.getTemplateSDK());

        redisService.addCouponToCache(request.getUserId(), Collections.singletonList(newCoupon),
                CouponStatus.AVAILABLE.getCode());
        return newCoupon;
    }

    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        List<SettlementInfo.CouponAndTemplateInfo> infos = info.getCouponAndTemplateInfos();
        if (CollectionUtils.isNotEmpty(infos)) {
            log.info("Users check out without using coupon");
            double sum = 0.0;
            for (ProductInfo productInfo : info.getProductInfoList()) {
                sum += productInfo.getCount() * productInfo.getPrice();
            }
            info.setCost(remain2Decimals(sum));

        }
        // Check if user owns this coupon
        List<Coupon> coupons = findUserOwnCouponByStatus(info.getUserId(), CouponStatus.AVAILABLE.getCode());
        // id - coupon id
        Map<Integer, Coupon> id2Coupon = coupons.stream()
                .collect(Collectors.toMap(Coupon::getId, Function.identity()));
        if (MapUtils.isEmpty(id2Coupon) || !CollectionUtils.isSubCollection(
                infos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId).collect(Collectors.toList()),
                id2Coupon.keySet())) {
            log.info("{}", id2Coupon.keySet());
            log.info("{}", infos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId).collect(Collectors.toList()));
            log.error("User doesn't have this coupon");
            throw new CouponException("User doesn't have this coupon");
        }
        log.debug("Current user owns this settlement coupon {}", infos.size());

        List<Coupon> settleCoupons = new ArrayList<>(infos.size());
        infos.forEach(i -> {
            settleCoupons.add(id2Coupon.get(i.getId()));
        });

        SettlementInfo processedInfo = settlementClient.computeRule(info).getData();
        if (processedInfo.getPaid() && CollectionUtils.isNotEmpty(processedInfo.getCouponAndTemplateInfos())) {
            log.info("Applied Coupon:{}, {}", info.getUserId(), JSON.toJSONString(settleCoupons));
            // Update cache, because the coupon has been used, should be deleted from available coupon cache,
            // and then add to used coupon cache
            redisService.addCouponToCache(info.getUserId(), settleCoupons, CouponStatus.USED.getCode());

            // Update DB
            kafkaTemplate.send(Constant.TOPIC,
                    JSON.toJSONString(new CouponKafkaMessage(
                            CouponStatus.USED.getCode(),
                            settleCoupons.stream()
                                    .map(Coupon::getId)
                                    .collect(Collectors.toList()))));
        }

        return processedInfo;
    }

    private double remain2Decimals(double value) {
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
