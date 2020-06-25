package com.shopping.coupon.converter;

import com.shopping.coupon.constant.DistributionTarget;

import javax.persistence.AttributeConverter;

/**
 * Platform Converter
 * AttributeConverter(X, Y)
 * X: Entity Attribute Type
 * Y: Database Attribute Type
 */
public class DistributionTargetConverter implements AttributeConverter<DistributionTarget, Integer> {


    // convert entity attribute type to corresponding database type, insert and update operation
    @Override
    public Integer convertToDatabaseColumn(DistributionTarget target) {
        return target.getCode();
    }

    // convert database attribute type to entity, search operation
    @Override
    public DistributionTarget convertToEntityAttribute(Integer code) {
        return DistributionTarget.of(code);
    }
}