package com.shopping.coupon.service;

import com.shopping.coupon.entity.Coupon;
import com.shopping.coupon.exception.CouponException;

import java.util.List;

public interface IRedisService {

    /**
     * get user coupons based by the status
     *
     * @param userId user id
     * @param status status
     * @return coupon list
     */
    List<Coupon> getCachedCoupons(Long userId, Integer status);

    /**
     * Avoid cache penetration, save a empty coupon to cache
     *
     * @param userId user id
     * @param status status
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * Try to get a coupon code from cache
     *
     * @param templateId template Id - primary key
     * @return coupon code
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * Save coupon to cache
     *
     * @param userId  user id
     * @param coupons coupon
     * @param status  coupon status
     * @return number of coupons which saves successfully
     * @throws CouponException exception
     */
    Integer addCouponToCache(Long userId, List<Coupon> coupons,
                             Integer status) throws CouponException;
}
