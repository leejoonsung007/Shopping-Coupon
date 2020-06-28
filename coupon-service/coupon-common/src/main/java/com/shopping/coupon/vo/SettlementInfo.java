package com.shopping.coupon.vo;

import com.sun.tools.javac.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * checkout info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInfo {

    private Long userId;

    private List<ProductInfo> productInfoList;

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
