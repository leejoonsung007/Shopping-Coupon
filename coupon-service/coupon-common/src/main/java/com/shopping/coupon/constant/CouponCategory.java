package com.shopping.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum  CouponCategory {

    OFF("$$ OFF Every $$ coupon", "001"),
    DISCOUNT("Discount coupon", "002"),
    CASH_BACK("Cash back", "003");


    private String description;

    private String code;

    public static CouponCategory of(String code) {
        // throw NullPointerException exception is code is null
        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not existed"));
    }
}
