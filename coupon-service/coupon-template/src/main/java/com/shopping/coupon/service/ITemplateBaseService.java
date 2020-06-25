package com.shopping.coupon.service;

import com.shopping.coupon.entity.CouponTemplate;
import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.Map;
import java.util.List;

/**
 * Coupon template basic service (view, delete)
 */
public interface ITemplateBaseService {

    /**
     * Get coupon template info by id
     * @param id
     * @return couponTemplate
     * @throws CouponException exception
     */
    CouponTemplate findCouponTemplateInfo(Integer id) throws CouponException;

    /**
     * Get all available templates
     * @return Coupon Template list
     */
    List<CouponTemplateSDK> findAllAvailableTemplates();

    /**
     * Get the mapping between ids and CouponTemplateSDK
     * @param ids
     * @return Map<key: template id, value: CouponTemplateSDK>
     */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);

    //TODO delete a coupon template
}
