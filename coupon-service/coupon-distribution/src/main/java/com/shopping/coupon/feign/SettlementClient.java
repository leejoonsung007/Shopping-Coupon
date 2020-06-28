package com.shopping.coupon.feign;

import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.feign.hystrix.SettlementClientHystrix;
import com.shopping.coupon.vo.CommonResponse;
import com.shopping.coupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Settlement microservice Feign interface
 */
@FeignClient(value = "eureka-client-coupon-settlement", fallback = SettlementClientHystrix.class)
public interface SettlementClient {

    @RequestMapping(value="/coupon-settlement/settlement/compute", method = RequestMethod.POST)
    CommonResponse<SettlementInfo> computeRule(@RequestBody SettlementInfo settlementInfo) throws CouponException;
}
