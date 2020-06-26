package com.shopping.coupon.service;

import com.alibaba.fastjson.JSON;
import com.shopping.coupon.TemplateApplicationTest;
import com.shopping.coupon.constant.CouponCategory;
import com.shopping.coupon.constant.DistributionTarget;
import com.shopping.coupon.constant.EXPType;
import com.shopping.coupon.constant.Platform;
import com.shopping.coupon.vo.TemplateRequest;
import com.shopping.coupon.vo.TemplateRule;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class CreateTemplateServiceTest extends TemplateApplicationTest {

    @Autowired
    private ICreateTemplateService createTemplateService;

    @Test
    public void CreateTemplate() throws Exception {
        System.out.println(JSON.toJSONString(createTemplateService.createTemplate(createFakeTemplateRequest())));
        // keep thread alive until async tasks finish
        Thread.sleep(5000);
    }

    private TemplateRequest createFakeTemplateRequest() {
        TemplateRequest request = new TemplateRequest();
        request.setName("couponTemplate-" + new Date().getTime());
        request.setLogo("http://www.google.com");
        request.setDescription("coupon template test");
        request.setCategory(CouponCategory.OFF.getCode());
        request.setPlatform(Platform.PLATFORM_ONE.getCode());
        request.setCount(10000);
        request.setUserId(10001L);
        request.setTarget(DistributionTarget.ACTIVE.getCode());

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(EXPType.DYNAMIC.getCode(), 1,
                DateUtils.addDays(new Date(), 60).getTime()));

        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage("Leinster", "dublin",
                JSON.toJSONString(Arrays.asList("furniture", "stationery"))));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));

        request.setRule(rule);

        return request;
    }
}
