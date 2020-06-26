package com.shopping.coupon.service;

import com.alibaba.fastjson.JSON;
import com.shopping.coupon.TemplateApplicationTest;
import com.shopping.coupon.exception.CouponException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class TemplateBaseServiceTest extends TemplateApplicationTest {

    @Autowired
    private ITemplateBaseService templateBaseService;

    @Test
    public void findCouponTemplateInfo() throws CouponException {
        System.out.println(JSON.toJSONString(templateBaseService.findCouponTemplateInfo(10)));
    }

    @Test
    public void findAllAvailableTemplates() {
        System.out.println(JSON.toJSONString(templateBaseService.findAllAvailableTemplates()));
    }

    @Test
    public void findIds2TemplateSDK() {
        System.out.println(JSON.toJSONString(templateBaseService.findIds2TemplateSDK(Arrays.asList(10, 2, 3))));
    }

}
