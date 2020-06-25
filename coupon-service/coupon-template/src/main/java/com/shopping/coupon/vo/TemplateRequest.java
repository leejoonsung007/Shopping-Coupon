package com.shopping.coupon.vo;

import com.shopping.coupon.constant.CouponCategory;
import com.shopping.coupon.constant.DistributionTarget;
import com.shopping.coupon.constant.Platform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * admin creates the coupon template
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {

    private String name;

    private String logo;

    private String description;

    private String category;

    private Integer platform;

    private Integer count;

    private Long userId;

    // target user
    private Integer target;

    private TemplateRule rule;

    // validate the template request
    public boolean validate() {
        boolean stringValid = StringUtils.isNoneEmpty(name)
                && StringUtils.isNoneEmpty(logo)
                && StringUtils.isNoneEmpty(description);
        boolean enumValid = CouponCategory.of(category) != null
                && Platform.of(platform) != null
                && DistributionTarget.of(target) != null;
        boolean numValid = count > 0 && userId > 0;
        return stringValid && enumValid && numValid && rule.validate();
    }

}
