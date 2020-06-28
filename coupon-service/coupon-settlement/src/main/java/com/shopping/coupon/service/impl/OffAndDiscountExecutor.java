package com.shopping.coupon.service.impl;


import com.alibaba.fastjson.JSON;
import com.shopping.coupon.constant.CouponCategory;
import com.shopping.coupon.constant.RuleFlag;
import com.shopping.coupon.service.AbstractExecutor;
import com.shopping.coupon.service.RuleExecutor;
import com.shopping.coupon.vo.ProductInfo;
import com.shopping.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OffAndDiscountExecutor extends AbstractExecutor implements RuleExecutor {

    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.OFF_DISCOUNT;
    }

    /**
     * stacked coupons check
     *
     * @param settlementInfo settlement info
     * @return true/false
     */
    @Override
    protected boolean canProductApplyCoupon(SettlementInfo settlementInfo) {
        List<Integer> itemTypeList = settlementInfo.getProductInfos().stream()
                .map(ProductInfo::getType).collect(Collectors.toList());

        List<Integer> couponSupportedCategoryType = new ArrayList<>();

        settlementInfo.getCouponAndTemplateInfos().forEach(ct -> {
            couponSupportedCategoryType.addAll(JSON.parseObject(
                    ct.getTemplate().getRule().getUsage().getItemType(), List.class
            ));
        });

        // if users want to use the stacked coupon, all items should meet stacked coupon usage rule
        // the Complement of itemTypeList and couponSupportedCategoryType should be empty
        return CollectionUtils.isEmpty(CollectionUtils.subtract(itemTypeList, couponSupportedCategoryType));
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double itemsTotalPrice = retain2Decimals(computeTotalCost(settlement.getProductInfos()));
        SettlementInfo checkout = processProductCannotApplyCoupon(settlement, itemsTotalPrice);

        if (checkout != null) {
            log.debug("Stacked coupon cannot be applied");
            return checkout;
        }

        SettlementInfo.CouponAndTemplateInfo off = null;
        SettlementInfo.CouponAndTemplateInfo discount = null;

        for (SettlementInfo.CouponAndTemplateInfo info : settlement.getCouponAndTemplateInfos()) {
            if (CouponCategory.of(info.getTemplate().getCategory()) == CouponCategory.OFF) {
                off = info;
            } else {
                discount = info;
            }
        }

        assert off != null;
        assert discount != null;

        if (!canCouponsUseTogether(off, discount)) {
            log.debug("Current stacked coupon can be used");
            settlement.setCost(itemsTotalPrice);
            // remove stacked coupon
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        // a list to record used coupon and then update data in settlement
        List<SettlementInfo.CouponAndTemplateInfo> usedCouponList = new ArrayList<>();
        double offBase = (double) off.getTemplate().getRule().getDiscount().getBase();
        double offQuota = (double) off.getTemplate().getRule().getDiscount().getQuota();

        // Apply off coupon
        double finalPrice = itemsTotalPrice;
        if (finalPrice > offBase) {
            finalPrice -= offQuota;
            usedCouponList.add(off);
        }

        // Apply discount coupon
        double discountQuota = (double) discount.getTemplate().getRule().getDiscount().getQuota();
        finalPrice *= discountQuota * 1.0 / 100;
        usedCouponList.add(discount);

        settlement.setCouponAndTemplateInfos(usedCouponList);
        settlement.setCost(retain2Decimals(Math.max(finalPrice, minCost())));
        log.debug("Use Stacked Coupon successful, price drops down from {} to {}",
                itemsTotalPrice, settlement.getCost());

        return settlement;
    }

    /**
     * check if these two coupons can be used together
     *
     * @param off      off coupon
     * @param discount discount coupon
     * @return true/false
     */
    private boolean canCouponsUseTogether(SettlementInfo.CouponAndTemplateInfo off,
                                          SettlementInfo.CouponAndTemplateInfo discount) {
        String offKey = off.getTemplate().getKey() + String.format("%4d", off.getTemplate().getId());
        String discountKey = discount.getTemplate().getKey() + String.format("%4d", discount.getTemplate().getId());

        List<String> canUseTogetherKeysForOff = new ArrayList<>();
        canUseTogetherKeysForOff.add(offKey);
        canUseTogetherKeysForOff.addAll(JSON.parseObject(off.getTemplate().getRule().getWeight(), List.class));

        List<String> canUseTogetherKeysForDiscount = new ArrayList<>();
        canUseTogetherKeysForDiscount.add(discountKey);
        canUseTogetherKeysForDiscount.addAll(JSON.parseObject(discount.getTemplate().getRule().getWeight(), List.class));

        return CollectionUtils.isSubCollection(Arrays.asList(offKey, discountKey), canUseTogetherKeysForOff) ||
                CollectionUtils.isSubCollection(Arrays.asList(offKey, discountKey), canUseTogetherKeysForDiscount);
    }
}
