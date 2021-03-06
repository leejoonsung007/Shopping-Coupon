package com.shopping.coupon.service;

import com.shopping.coupon.entity.Coupon;
import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.vo.AcquireTemplateRequest;
import com.shopping.coupon.vo.CouponTemplateSDK;
import com.shopping.coupon.vo.SettlementInfo;

import java.util.List;

public interface IUserService {

    /**
     * Get coupon info by userId and status
     *
     * @param userId user id
     * @param status status
     * @return coupon list
     * @throws CouponException exception
     */
    List<Coupon> findUserOwnCouponByStatus(Long userId, Integer status) throws CouponException;

    /**
     * Get user available coupon template
     *
     * @param userId user id
     * @return CouponTemplateSDK
     * @throws CouponException exception
     */
    List<CouponTemplateSDK> findAvailableCouponTemplate(Long userId) throws CouponException;

    /**
     * User collects a coupon
     *
     * @param request coupon acquire request
     * @return coupon
     * @throws CouponException exception
     */
    Coupon collectCoupon(AcquireTemplateRequest request) throws CouponException;


    /**
     * @param info
     * @return settlement infp
     * @throws CouponException exception
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;

}
