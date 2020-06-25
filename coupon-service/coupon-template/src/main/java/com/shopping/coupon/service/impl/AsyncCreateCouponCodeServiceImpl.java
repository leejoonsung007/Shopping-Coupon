package com.shopping.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.shopping.coupon.constant.Constant;
import com.shopping.coupon.dao.CouponTemplateDao;
import com.shopping.coupon.entity.CouponTemplate;
import com.shopping.coupon.service.IAsyncCreateCouponCodeService;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AsyncCreateCouponCodeServiceImpl implements IAsyncCreateCouponCodeService {

    private CouponTemplateDao couponTemplateDao;

    private StringRedisTemplate redisTemplate;

    @Autowired
    public AsyncCreateCouponCodeServiceImpl(CouponTemplateDao couponTemplateDao,
                                            StringRedisTemplate redisTemplate) {
        this.couponTemplateDao = couponTemplateDao;
        this.redisTemplate = redisTemplate;
    }


    @Async("getAsyncExecutor")
    @Override
    public void asyncCreateCouponCode(CouponTemplate template) {

        Stopwatch stopwatch = Stopwatch.createStarted();

        Set<String> couponCodes = createCouponCode(template);

        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE,
                template.getId().toString());
        log.info("Push Coupon Code To Redis: {}", redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));
        template.setAvailable(true);
        couponTemplateDao.save(template);

        stopwatch.stop();
        log.info("Construct Coupon code By Template Cost: {}ms",
                stopwatch.elapsed(TimeUnit.MICROSECONDS));

        //TODO send message/email to notify coupon template is available
        log.info("CouponTemplate({}) is Available", template.getId());
    }

    /**
     * create a coupon code (18 digits = platform code (3 digits) + category code (1 digit) +
     * time(6 digits = 200101) + random number(8 digits)
     * @param template template entity
     * @return coupon code set which has the same length as template.count
     */
    private Set<String> createCouponCode(CouponTemplate template) {

        Stopwatch stopwatch = Stopwatch.createStarted();

        Set<String> result = new HashSet<>(template.getCount());

        String platformCode = template.getPlatform().getCode().toString();
        String categoryCode = template.getCategory().getCode();
        String prefix4 = platformCode + categoryCode;
        String date = new SimpleDateFormat("yyMMdd").format(template.getCreateTime());

        // generate specific numbers of templates
        for (int i = 0; i != template.getCount(); ++i) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        // low efficiency due to size check
        while(result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        assert  result.size() == template.getCount();

        stopwatch.stop();
        log.info("Build Coupon Code Cose: {}ms", stopwatch.elapsed(TimeUnit.MICROSECONDS));

        return result;
    }

    /**
     * generated suffix 14 digits of the coupon code
     * @param date date for coupon creation
     * @return 14 digits of coupon code
     */
    private String buildCouponCodeSuffix14(String date) {
        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};

        List<Character> chars = date.chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6 = chars.stream()
                .map(Objects::toString)
                .collect(Collectors.joining());

        String suffix8 = RandomStringUtils.random(1, bases) + RandomStringUtils.randomNumeric(7);

        return mid6 + suffix8;
    }
}
