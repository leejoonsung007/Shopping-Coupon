package com.shopping.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo {

    // productTypes
    private Integer type;

    private Double price;

    private Integer count;

    private String name;

    private String description;
}
