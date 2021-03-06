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
public class CashBackExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.CASH_BACK;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double itemsTotalCost = retain2Decimals(computeTotalCost(settlement.getProductInfos()));

        SettlementInfo checkout = processProductCannotApplyCoupon(settlement, itemsTotalCost);
        if (checkout != null) {
            log.debug("Cash Back Coupon Template is not satisfied with the usage rule");
            return checkout;
        }

        // Cash back coupon can be applied without restriction
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos().get(0).getTemplate();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        // Compute the final price
        settlement.setCost(retain2Decimals(Math.max(itemsTotalCost - quota, minCost())));
        log.debug("After applying cash back coupon, total price goes down from {} to {}",
                itemsTotalCost, settlement.getCost());
        return settlement;
    }
}
