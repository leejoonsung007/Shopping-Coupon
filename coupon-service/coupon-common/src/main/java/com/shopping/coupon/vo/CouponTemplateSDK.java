package com.shopping.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Coupon Template info definition used in different microservice
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateSDK {

    // coupon template primary key
    private Integer id;

    private String name;

    private String logo;

    private String description;

    private String category;

    private Integer platform;

    private String key;

    private Integer target;

    private TemplateRule rule;
}
