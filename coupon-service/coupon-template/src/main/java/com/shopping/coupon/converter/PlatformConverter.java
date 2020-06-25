package com.shopping.coupon.converter;

import com.shopping.coupon.constant.Platform;

import javax.persistence.AttributeConverter;

/**
 * Platform Converter
 * AttributeConverter(X, Y)
 * X: Entity Attribute Type
 * Y: Database Attribute Type
 */
public class PlatformConverter implements AttributeConverter<Platform, Integer> {


    // convert entity attribute type to corresponding database type, insert and update operation
    @Override
    public Integer convertToDatabaseColumn(Platform platform) {
        return platform.getCode();
    }

    // convert database attribute type to entity, search operation
    @Override
    public Platform convertToEntityAttribute(Integer code) {
        return Platform.of(code);
    }
}