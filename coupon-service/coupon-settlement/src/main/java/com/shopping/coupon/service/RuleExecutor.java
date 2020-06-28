package com.shopping.coupon.service;

import com.shopping.coupon.constant.RuleFlag;
import com.shopping.coupon.vo.SettlementInfo;

public interface RuleExecutor {

    /**
     * link to different compute rules
     * @return rule flag
     */
    RuleFlag ruleConfig();

    /**
     * rule for using the coupon
     * @param settlement contains selected coupon
     * @return modified settlment info
     */
    SettlementInfo computeRule(SettlementInfo settlement);
}
