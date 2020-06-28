package com.shopping.coupon.feign.hystrix;

import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.feign.SettlementClient;
import com.shopping.coupon.vo.CommonResponse;
import com.shopping.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Settlement Client circuit-breaker
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {

    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlementInfo) throws CouponException {
        log.error("[eureka-client-coupon-settlement] ComputeRule " + "request error");

        settlementInfo.setPaid(false);
        settlementInfo.setCost(-1.0);

        return new CommonResponse<>(-1, "[eureka-client-coupon-settlement] Request error",
                settlementInfo);
    }
}
