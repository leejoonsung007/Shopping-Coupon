package com.shopping.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.shopping.coupon.constant.CouponStatus;
import com.shopping.coupon.converter.CouponStatusConverter;
import com.shopping.coupon.serialization.CouponSerialize;
import com.shopping.coupon.vo.CouponTemplateSDK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon")
@JsonSerialize(using = CouponSerialize.class)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // logic foreign key
    @Column(name = "template_id", nullable = false)
    private Integer templateId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    @CreatedDate
    @Column(name = "assign_time", nullable = false)
    private Date assignTime;

    @Column(name = "status", nullable = false)
    @Convert(converter = CouponStatusConverter.class)
    private CouponStatus status;

    // Transient indicates couponTemplateSDK doesn't belong to current table
    @Transient
    private CouponTemplateSDK templateSDK;

    /**
     * @return an invalid coupon object
     */
    public static Coupon invalidCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }


    public Coupon(Integer templateId, Long userId, String couponCode,
                  CouponStatus status) {
        this.templateId = templateId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.status = status;
    }
}
