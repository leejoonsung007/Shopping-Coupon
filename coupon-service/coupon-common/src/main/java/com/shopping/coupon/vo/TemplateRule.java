package com.shopping.coupon.vo;

import com.shopping.coupon.constant.EXPType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * coupon template rule object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRule {

    private Expiration expiration;

    private Discount discount;

    // the number of coupon each person can receive
    private Integer limitation;

    private Usage usage;

    // weight to make stack coupons,
    // the same type of coupons cannot be used together - list[coupon unique code]
    private String weight;

    public boolean validate() {
        return expiration.validate()
                && discount.validate()
                && limitation > 0
                && usage.validate()
                && StringUtils.isNotEmpty(weight);
    }

    // Inner class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Expiration {

        // EXPType - code
        private Integer period;

        // Valid duration, used in dynamic coupon
        private Integer duration;

        // Expired date, used in regular/dynamic coupon
        private Long expiredDate;

        //TODO expiredDate should be a future time
        boolean validate() {
            return EXPType.of(period) != null && duration > 0 && expiredDate > 0;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Discount {
        // The amount of discount
        private Integer quota;

        //$$ off every base amount
        private Integer base;

        boolean validate() {
            return quota > 0 && base > 0;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {

        // Area
        private String province;

        // City
        private String city;

        // Item Type, list[item, item]
        private String ItemType;

        boolean validate() {
            return StringUtils.isNotEmpty(province)
                    && StringUtils.isNotEmpty(city)
                    && StringUtils.isNotEmpty(ItemType);
        }
    }
}
