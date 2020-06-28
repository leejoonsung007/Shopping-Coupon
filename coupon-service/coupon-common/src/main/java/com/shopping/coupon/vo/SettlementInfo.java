package com.shopping.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * checkout info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInfo {

    private Long userId;

    private List<ProductInfo> productInfos;

    private List<CouponAndTemplateInfo> couponAndTemplateInfos;

    private Double cost;

    private Boolean paid;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponAndTemplateInfo {

        private Integer id;

        private CouponTemplateSDK template;


    }
}
