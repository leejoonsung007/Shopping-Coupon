package com.shopping.coupon.service.impl;

import com.shopping.coupon.constant.RuleFlag;
import com.shopping.coupon.service.AbstractExecutor;
import com.shopping.coupon.service.RuleExecutor;
import com.shopping.coupon.vo.CouponTemplateSDK;
import com.shopping.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class OffExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.OFF;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double itemsTotalCost = retain2Decimals(computeTotalCost(settlement.getProductInfos()));

        SettlementInfo checkout = processProductCannotApplyCoupon(settlement, itemsTotalCost);
        if (checkout != null) {
            log.debug("Off Coupon Template is not satisfied with the usage rule");
            return checkout;
        }

        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos().get(0).getTemplate();
        double base = (double) templateSDK.getRule().getDiscount().getBase();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        // coupon can be used
        if (itemsTotalCost < base) {
            settlement.setCost(itemsTotalCost);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }
        // coupon cannot be used
        settlement.setCost(retain2Decimals(Math.max((itemsTotalCost - quota), minCost())));
        log.debug("After applying coupon, total price change from {} to {}", itemsTotalCost, settlement.getCost());

        return settlement;
    }
}
