package com.shopping.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CouponStatus {

    AVAILABLE("available", 1),
    USED("used", 2),
    EXPIRED("expired/never used", 3);

    private String description;

    private Integer code;

    /**
     * get coupon status object by code
     * @param code code
     * @return coupon status object
     */
    public static CouponStatus of(Integer code) {

        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(status -> status.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + "is not existed")
                );
    }
}
