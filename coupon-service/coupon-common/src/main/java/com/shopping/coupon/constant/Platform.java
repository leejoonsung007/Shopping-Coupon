package com.shopping.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Platform {

    PLATFORM_ONE("Platform one", 1),
    PLATFORM_TWO("Platform two", 2);

    private String description;

    private Integer code;

    public static Platform of(Integer code) {
        // throw NullPointerException exception is code is null
        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not existed"));
    }

}
