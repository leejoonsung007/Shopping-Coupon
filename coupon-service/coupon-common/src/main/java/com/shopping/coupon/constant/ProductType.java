package com.shopping.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ProductType {

    STATIONERY("stationery", 1),
    FOOD("food", 2),
    HOME("home", 3),
    OTHERS("others", 4),
    ALL("all", 5);

    private String description;

    private Integer code;

    public static ProductType of(Integer code) {

        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(product -> product.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "is not existed"));
    }
}
