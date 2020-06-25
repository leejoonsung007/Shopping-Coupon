package com.shopping.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.shopping.coupon.constant.CouponCategory;
import com.shopping.coupon.constant.DistributionTarget;
import com.shopping.coupon.constant.Platform;
import com.shopping.coupon.converter.CouponCategoryConverter;
import com.shopping.coupon.converter.DistributionTargetConverter;
import com.shopping.coupon.converter.PlatformConverter;
import com.shopping.coupon.converter.RuleConverter;
import com.shopping.coupon.serialization.CouponTemplateSerialize;
import com.shopping.coupon.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * coupon template class
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
// hibernate auto creates some values
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon_template")
@JsonSerialize(using = CouponTemplateSerialize.class)
public class CouponTemplate implements Serializable {

    // auto increase primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "expired", nullable = false)
    private Boolean expired;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "logo", nullable = false)
    private String logo;

    @Column(name = "intro", nullable = false)
    private String description;

    @Column(name = "category", nullable = false)
    @Convert(converter = CouponCategoryConverter.class)
    private CouponCategory category;

    @Column(name = "platform", nullable = false)
    @Convert(converter = PlatformConverter.class)
    private Platform platform;

    @Column(name = "coupon_count", nullable = false)
    private Integer count;

    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "template_key", nullable = false)
    private String key;

    @Column(name = "target", nullable = false)
    @Convert(converter = DistributionTargetConverter.class)
    private DistributionTarget target;

    @Column(name = "rule", nullable = false)
    @Convert(converter = RuleConverter.class)
    private TemplateRule rule;

    public CouponTemplate(String name, String logo, String description, String category,
                          Integer platform, Integer count, Long userId,
                          Integer target, TemplateRule rule) {
        this.available = false;
        this.expired = false;
        this.name = name;
        this.logo = logo;
        this.description = description;
        this.category = CouponCategory.of(category);
        this.platform = Platform.of(platform);
        this.count = count;
        this.userId = userId;
        this.key = platform.toString() + category + new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.target = DistributionTarget.of(target);
        this.rule = rule;
    }


}
