package com.shopping.coupon.controller.userService;

import com.alibaba.fastjson.JSON;
import com.shopping.coupon.entity.Coupon;
import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.service.IUserService;
import com.shopping.coupon.vo.AcquireTemplateRequest;
import com.shopping.coupon.vo.CouponTemplateSDK;
import com.shopping.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class CouponDistributionController {

    private IUserService userService;

    @Autowired
    public CouponDistributionController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * Get all user own coupons by coupon status
     * @param userId user id
     * @param status coupon status
     * @return coupon list
     * @throws CouponException exception
     */
    @RequestMapping("/coupons")
    public List<Coupon> findUserOwnCouponByStatus(@RequestParam("userId") Long userId,
                                           @RequestParam("status") Integer status) throws CouponException {
        log.info("Get user coupon userId {}, status {}", userId, status);
        return userService.findUserOwnCouponByStatus(userId, status);
    }

    /**
     * Get all coupons which user can collect
     * @param userId user id
     * @return CouponTemplateSDK list
     * @throws CouponException exception
     */
    @RequestMapping("/template")
    public List<CouponTemplateSDK> findAvailableCouponTemplate(@RequestParam Long userId) throws CouponException {
        log.info("Get all coupons that user {} can collect", userId);
        return userService.findAvailableCouponTemplate(userId);
    }

    /**
     * Try to Collect a coupon
     * @param request acquire coupon from coupon template
     * @return coupon
     * @throws CouponException exception
     */
    @PostMapping("/coupon/collect")
    public Coupon collectCoupon(@RequestBody AcquireTemplateRequest request) throws CouponException {
        log.info("User try to collect a coupon {}", JSON.toJSONString(request));
        return userService.collectCoupon(request);
    }

    /**
     * settlement
     * @param info settlement info
     * @return settlement info
     * @throws CouponException request
     */
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo info) throws CouponException {
        log.info("User try to collect a coupon {}", JSON.toJSONString(info));
        return userService.settlement(info);
    }
}
