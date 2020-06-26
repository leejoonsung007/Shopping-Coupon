package com.shopping.coupon.converter;

import com.shopping.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Coupon Category Converter
 * AttributeConverter(X, Y)
 * X: Entity Attribute Type
 * Y: Database Attribute Type
 */
@Converter
public class CouponCategoryConverter implements AttributeConverter<CouponCategory, String> {

    // convert entity attribute type to corresponding database type, insert and update
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    // convert database attribute type to entity, search
    @Override
    public CouponCategory convertToEntityAttribute(String code) {
        return CouponCategory.of(code);
    }
}
