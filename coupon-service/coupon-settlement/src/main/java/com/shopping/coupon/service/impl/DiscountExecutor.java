package com.shopping.coupon.service.impl;

import com.shopping.coupon.constant.RuleFlag;
import com.shopping.coupon.service.AbstractExecutor;
import com.shopping.coupon.service.RuleExecutor;
import com.shopping.coupon.vo.CouponTemplateSDK;
import com.shopping.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DiscountExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.DISCOUNT;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double itemsTotalCost = retain2Decimals(computeTotalCost(settlement.getProductInfos()));
        SettlementInfo checkout = processProductCannotApplyCoupon(settlement, itemsTotalCost);

        if (checkout != null) {
            log.debug("Discount Coupon Template is not satisfied with the usage rule");
            return checkout;
        }

        // discount coupon can be used without limitation
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos().get(0).getTemplate();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        settlement.setCost(retain2Decimals(Math.max((itemsTotalCost * (quota * 1.0 / 100)), minCost())));
        log.debug("After applying discount coupon, total price goes down from {} to {}",
                itemsTotalCost, settlement.getCost());
        return settlement;
    }
}
