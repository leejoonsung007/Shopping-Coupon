package com.shopping.coupon.service;

import com.alibaba.fastjson.JSON;
import com.shopping.coupon.vo.ProductInfo;
import com.shopping.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Common methods
 */
public class AbstractExecutor {

    /**
     * Check if the product can apply the coupon code (Check the executor rule)
     * Single coupon
     * @param settlementInfo settlement info
     * @return true/false
     */
    protected boolean canProductApplyCoupon(SettlementInfo settlementInfo) {
        List<Integer> productType = settlementInfo.getProductInfos().stream()
                .map(ProductInfo::getType)
                .collect(Collectors.toList());

        // coupon using rule
        List<Integer> couponSupportedType = JSON.parseObject(settlementInfo.getCouponAndTemplateInfos().get(0)
                .getTemplate().getRule().getUsage().getItemType(), List.class);

       return CollectionUtils.isNotEmpty(
                CollectionUtils.intersection(productType, couponSupportedType)
        );
    }

    /**
     * Process Product settlement cannot apply the coupon
     * @param settlement settlement info passed by user
     * @param productTotalCost total cost
     * @return modified settlement info
     */
    protected SettlementInfo processProductCannotApplyCoupon(SettlementInfo settlement,
                                                             double productTotalCost) {
        boolean canProductApplyCoupon = canProductApplyCoupon(settlement);
        if (!canProductApplyCoupon) {
            settlement.setCost(productTotalCost);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }
        return null;
    }

    /**
     * Compute the total cost of items
     * @param productInfoList item info List
     * @return total cost
     */
    protected double computeTotalCost(List<ProductInfo> productInfoList) {
       return productInfoList.stream().mapToDouble(
                product -> product.getPrice() * product.getCount()
        ).sum();
    }

    /**
     * keep 2 decimals
     * @param value value to process
     * @return value after processing
     */
    protected double retain2Decimals(double value) {

        return new BigDecimal(value).setScale(
                2, BigDecimal.ROUND_HALF_UP
        ).doubleValue();
    }

    /**
     * handle edge case - after applying coupon, cost becomes a minus value - 10, should be reset to 0.1;
     * @return
     */
    protected double minCost() {
        return 0.1;
    }
}
