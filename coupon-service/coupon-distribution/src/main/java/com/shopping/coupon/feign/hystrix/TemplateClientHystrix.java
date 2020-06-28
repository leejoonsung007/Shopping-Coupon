package com.shopping.coupon.feign.hystrix;

import com.shopping.coupon.feign.TemplateClient;
import com.shopping.coupon.vo.CommonResponse;
import com.shopping.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Template Client circuit-breaker
 */
@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {

    // Fallback
    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllAvailableTemplate() {
        log.error("[eureka-client-coupon-template] findAllAvailableTemplate " + "request error");
        return new CommonResponse<>(-1, "[eureka-client-coupon-template] request error",
                Collections.emptyList());
    }

    // Fallback
    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-coupon-template] findIds2TemplateSDK " + "request error");
        return new CommonResponse<>(-1, "[eureka-client-coupon-template] request error",
                new HashMap<>());
    }
}
