package com.shopping.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum DistributionTarget {

    ACTIVE("Users need to get the coupon by themselves", 1),
    PASSIVE("Users no need to get the coupon by themselves", 2);

    private String description;

    private Integer code;

    public static DistributionTarget of(Integer code) {
        // throw NullPointerException exception is code is null
        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not existed"));
    }
}
