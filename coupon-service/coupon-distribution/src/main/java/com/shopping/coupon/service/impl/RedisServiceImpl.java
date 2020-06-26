package com.shopping.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.shopping.coupon.constant.Constant;
import com.shopping.coupon.constant.CouponStatus;
import com.shopping.coupon.entity.Coupon;
import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.hibernate.action.internal.CollectionAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {

    // Redis client
    private StringRedisTemplate redisTemplate;

    @Autowired
    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {

        log.info("Get Coupons From Cache:{}, {}", userId, status);
        String redisKey = status2RedisKey(status, userId);
        List<String> coupons = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(coupons)) {
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }

        return coupons.stream()
                .map(coupon -> JSON.parseObject(coupon, Coupon.class))
                .collect(Collectors.toList());
    }

    /**
     * save a empty coupon to cache
     * Avoid cache penetration
     *
     * @param userId user id
     * @param status status
     */
    @Override
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save empty list to cache for User:{}, status:{}", userId, JSON.toJSONString(status));
        Map<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        // use SessionCallback to save value Redis to pipeline
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);
                });
                return null;
            }
        };
        log.info("Redis pipeline Execute Resukt:{}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    }

    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {

        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, templateId.toString());
        // coupon code without order, coupon code could be null
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire Code Code:{}, {}, {}", templateId, redisKey, couponCode);

        return couponCode;
    }

    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupons to cache:{}, {}, {}", userId, JSON.toJSONString(coupons), status);
        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case AVAILABLE:
                result = addCouponToCacheForAvailable(userId, coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, coupons);
                break;
        }
        return result;
    }

    /**
     * add new coupon to cache
     *
     * @param userId  user id
     * @param coupons coupon list
     * @return cached object number
     */
    private Integer addCouponToCacheForAvailable(Long userId, List<Coupon> coupons) {
        // user acquire a coupon
        log.debug("Add Coupon to Cache For Available");
        Map<String, String> needCachedObject = new HashMap<>(coupons.size());
        coupons.forEach(c ->
                needCachedObject.put(c.getId().toString(), JSON.toJSONString(c)));
        String redisKey = status2RedisKey(CouponStatus.AVAILABLE.getCode(), userId);
        redisTemplate.opsForHash().putAll(redisKey, needCachedObject);
        log.info("Add {} Coupon To Cache:{}, {}",
                needCachedObject.size(), userId, redisKey);
        redisTemplate.expire(redisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
        return needCachedObject.size();
    }

    /**
     * add used coupon to cache
     *
     * @param userId  user id
     * @param coupons coupons will be used
     * @return cache object number
     * @throws CouponException
     */
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons) throws CouponException {
        // user use current coupon
        // AVAILABLE - > USED cache
        log.debug("Add Coupon To Cache For Used");

        Map<String, String> needCachedForUsed = new HashMap<>(coupons.size());
        String redisKeyForAvailable = status2RedisKey(CouponStatus.AVAILABLE.getCode(), userId);
        String redisKeyForUsed = status2RedisKey(CouponStatus.USED.getCode(), userId);

        // Get the coupons of current user
        List<Coupon> curAvailableCoupons = getCachedCoupons(userId, CouponStatus.AVAILABLE.getCode());
        // CurUsableCoupons must have a invalid coupon
        assert curAvailableCoupons.size() > coupons.size();

        coupons.forEach(coupon -> needCachedForUsed.put(coupon.getId().toString(), JSON.toJSONString(coupon)));

        // check whether coupons will be used (paramsIds) can match cached coupons (curAvailableIds)
        List<Integer> curAvailableIds = curAvailableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        // check if paramsIds is curAvailableId sub collection
        if (!CollectionUtils.isSubCollection(paramIds, curAvailableIds)) {
            log.error("Current Coupon Is Not Equal to Cache:{} {} {}", userId, JSON.toJSONString(curAvailableIds),
                    JSON.toJSONString(paramIds));
            throw new CouponException("Current Coupon Is Not Equal to Cache");
        }

        List<String> needCleanKeys = paramIds.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations operations) throws DataAccessException {
                // add used coupon to used coupon cache
                operations.opsForHash().putAll(redisKeyForUsed, needCachedForUsed);
                // remove used coupon from available coupon cache
                operations.opsForHash().delete(redisKeyForAvailable, needCleanKeys.toArray());
                // reset user cache expired time
                operations.expire(redisKeyForUsed, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                operations.expire(redisKeyForAvailable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };

        log.info("Pipeline Execute Result:{}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

        return coupons.size();

    }

    /**
     * add expired coupon to cache
     *
     * @param userId  user id
     * @param coupons coupons will be used
     * @return cache object number
     * @throws CouponException
     */
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> coupons) throws CouponException {
        // expired coupon
        // AVAILABLE - > EXPIRED cache
        log.debug("Add Coupon To Cache For Expired");

        Map<String, String> needCachedForExpired = new HashMap<>(coupons.size());

        String redisKeyForAvailable = status2RedisKey(CouponStatus.AVAILABLE.getCode(), userId);
        String redisKeyForExpired = status2RedisKey(CouponStatus.EXPIRED.getCode(), userId);

        List<Coupon> curAvailableCoupon = getCachedCoupons(userId, CouponStatus.AVAILABLE.getCode());
        List<Coupon> curExpiredCoupon = getCachedCoupons(userId, CouponStatus.EXPIRED.getCode());

        // Current Available Coupon must have one invalid coupon
        assert curAvailableCoupon.size() > coupons.size();

        coupons.forEach(coupon -> needCachedForExpired.put(coupon.getId().toString(), JSON.toJSONString(coupon)));

        // check whether coupons will be used (paramsIds) can match cached coupons (curAvailableIds)
        List<Integer> curAvailableIds = curAvailableCoupon.stream()
            .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = curExpiredCoupon.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        // check if paramsIds is curAvailableId sub collection
        if (!CollectionUtils.isSubCollection(paramIds, curAvailableIds)) {
            log.error("Current Coupon Is Not Equal to Cache:{} {} {}", userId, JSON.toJSONString(curAvailableIds),
                    JSON.toJSONString(paramIds));
            throw new CouponException("Current Coupon Is Not Equal to Cache");
        }

        List<String> needCleanKeys = paramIds.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations operations) throws DataAccessException {

                // Add to expired coupon cache
                operations.opsForHash().putAll(redisKeyForExpired, needCachedForExpired);

                // Clean expired coupon from availabled cache
                operations.opsForHash().delete(redisKeyForAvailable, needCleanKeys.toArray());

                // Reset
                operations.expire(redisKeyForAvailable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                operations.expire(redisKeyForExpired, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("Pipeline Execute Result:{}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }

    /**
     * generate a random expiration time (For redis key)
     * aim to handle cache avalanche issue
     *
     * @param min min hour
     * @param max max hour
     * @return return a value between min and max [min, max]
     */
    private Long getRandomExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(
                min * 60 * 60, max * 60 * 60
        );
    }

    /**
     * generate redis key based on userId
     *
     * @param status status code
     * @param userId userId
     * @return redis key
     */
    private String status2RedisKey(Integer status, Long userId) {
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case AVAILABLE:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_AVAILABLE_COUPON, userId);
                break;
            case USED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_USED_COUPON, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_EXPIRED_COUPON, userId);
                break;
        }
        return redisKey;
    }
}
