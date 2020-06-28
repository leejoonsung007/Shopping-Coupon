package com.shopping.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RuleFlag {

    // Single Coupon
    OFF("$$ OFF Every $$ Coupon Rule"),
    DISCOUNT("Discount Coupon Rule"),
    CASH_BACK("Cash Back Rule"),

    // Stacked Coupons
    OFF_DISCOUNT("Off + Discount Rule");

    //TODO add more stacked coupons rule

    private String description;

}
