package com.shopping.coupon.converter;

import com.shopping.coupon.constant.CouponStatus;

import javax.persistence.AttributeConverter;

public class CouponStatusConverter implements AttributeConverter<CouponStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer code) {
        return CouponStatus.of(code);
    }
}
