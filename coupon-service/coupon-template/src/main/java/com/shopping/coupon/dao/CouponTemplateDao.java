package com.shopping.coupon.dao;

import com.shopping.coupon.entity.CouponTemplate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Definition for couponTemplate
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate, Integer> {

    // search a coupon template by template name
    CouponTemplate findByName(String templateName);

    // search all templates based on available and expired
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);

    // search all expired templates
    List<CouponTemplate> findAllByExpired(Boolean expired);

}