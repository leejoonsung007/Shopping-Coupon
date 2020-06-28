package com.shopping.coupon.service;

import com.alibaba.fastjson.JSON;
import com.shopping.coupon.DistributionApplicationTest;
import com.shopping.coupon.constant.CouponStatus;
import com.shopping.coupon.exception.CouponException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class userServiceTest extends DistributionApplicationTest {

    private Long fakeUserId = 20001L;

    @Autowired
    private IUserService userService;

    @Test
    public void testFindCouponByStatus() throws CouponException {
        System.out.println(JSON.toJSONString(userService.findUserOwnCouponByStatus(fakeUserId,
                CouponStatus.AVAILABLE.getCode())));
    }

    @Test
    public void FindAvailableTemplate() throws CouponException {
        System.out.println(JSON.toJSONString(
                userService.findAvailableCouponTemplate(fakeUserId)
        ));
    }
}
