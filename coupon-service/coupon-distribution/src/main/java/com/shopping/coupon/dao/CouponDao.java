package com.shopping.coupon.dao;

import com.shopping.coupon.constant.CouponStatus;
import com.shopping.coupon.entity.Coupon;
import com.sun.tools.javac.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository(Classname, primary key type)
 */
public interface CouponDao extends JpaRepository<Coupon, Integer> {

    /**
     * Find coupon by userId ans coupon status
     * @param userId userId
     * @param status status
     * @return coupon list
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);

}
