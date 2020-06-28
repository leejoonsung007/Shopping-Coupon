package com.shopping.coupon.vo;

import ch.qos.logback.core.rolling.helper.PeriodicityType;
import com.shopping.coupon.constant.CouponStatus;
import com.shopping.coupon.constant.EXPType;
import com.shopping.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.time.DateUtils;

import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * classify user coupons based on their status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {

    private List<Coupon> available;

    private List<Coupon> used;

    private List<Coupon> expired;

    // classify current coupons based on their status
    public static CouponClassify classify(List<Coupon> coupons) {
        List<Coupon> availableCouponList = new ArrayList<>(coupons.size());
        List<Coupon> usedCouponList = new ArrayList<>(coupons.size());
        List<Coupon> expiredCouponList = new ArrayList<>(coupons.size());

        // use the delay update strategy for user coupon expiration,
        // only updating the status when users check their coupon
        coupons.forEach(coupon -> {
                boolean isTimeExpire;
                long curTime = new Date().getTime();

                // handle fixed valid duration coupon
                if (coupon.getTemplateSDK().getRule().getExpiration().getPeriod().equals(EXPType.FIXED.getCode())) {
                    isTimeExpire = coupon.getTemplateSDK().getRule().getExpiration().getExpiredDate() <= curTime;
                }
                // handle dynamic valid duration coupon
                else {
                    isTimeExpire = DateUtils.addDays(coupon.getAssignTime(),
                            coupon.getTemplateSDK().getRule().getExpiration().getDuration()).getTime() <= curTime;
                }

                if (coupon.getStatus().equals(CouponStatus.USED)) {
                    usedCouponList.add(coupon);
                } else if (coupon.getStatus().equals(CouponStatus.EXPIRED) || isTimeExpire) {
                    expiredCouponList.add(coupon);
                } else {
                    availableCouponList.add(coupon);
                }
            }
        );
        return new CouponClassify(availableCouponList, usedCouponList, expiredCouponList);
    }
}
