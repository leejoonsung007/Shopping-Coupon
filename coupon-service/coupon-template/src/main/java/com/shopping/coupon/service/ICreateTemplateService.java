package com.shopping.coupon.service;

import com.shopping.coupon.entity.CouponTemplate;
import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.vo.TemplateRequest;

/**
 * Interface
 */
public interface ICreateTemplateService {

    /**
     * Create coupon template
     *
     * @param request template request parameters
     * @return coupon template entity
     * @throws CouponException exception
     */
    CouponTemplate createTemplate(TemplateRequest request)
            throws CouponException;
}
