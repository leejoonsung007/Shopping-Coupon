package com.shopping.coupon.feign;

import com.shopping.coupon.feign.hystrix.TemplateClientHystrix;
import com.shopping.coupon.vo.CommonResponse;
import com.shopping.coupon.vo.CouponTemplateSDK;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Coupon template microservice Feign interface
 */
@FeignClient(value = "eureka-client-coupon-template", fallback = TemplateClientHystrix.class)
public interface TemplateClient {

    /**
     * Find All Available Template
     * @return available coupon templates
     */
    @RequestMapping(value = "/coupon-template/template/sdk/all",
                    method = RequestMethod.GET)
    CommonResponse<List<CouponTemplateSDK>> findAllAvailableTemplate();

    /**
     * Get the mapping from ids to CouponTemplateSDK
     * @return
     */
    @RequestMapping(value = "/coupon-template//template/sdk/infos",
                    method = RequestMethod.GET)
    CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids);
}
