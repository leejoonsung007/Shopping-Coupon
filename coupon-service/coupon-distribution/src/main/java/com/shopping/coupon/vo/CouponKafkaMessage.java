package com.shopping.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * coupon kafka message object definition
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponKafkaMessage {

    private Integer status;

    // Coupon primary keys
    private List<Integer> ids;
}
