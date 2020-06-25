package com.shopping.coupon.service;

import com.shopping.coupon.entity.CouponTemplate;

public interface IAsyncCreateCouponCodeService {
    /**
     * Async create coupon based on coupon template
     * @param couponTemplate
     */
    void asyncCreateCouponCode(CouponTemplate couponTemplate);
}
