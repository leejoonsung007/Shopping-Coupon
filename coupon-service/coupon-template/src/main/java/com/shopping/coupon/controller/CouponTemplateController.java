package com.shopping.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.shopping.coupon.entity.CouponTemplate;
import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.service.ICreateTemplateService;
import com.shopping.coupon.service.ITemplateBaseService;
import com.shopping.coupon.vo.CouponTemplateSDK;
import com.shopping.coupon.vo.TemplateRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class CouponTemplateController {

    private ICreateTemplateService buildTemplateService;

    private ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(ICreateTemplateService buildTemplateService, ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    /**
     * Create coupon template api
     * 127.0.0.1:7001/coupon-template/template/create
     * Access via gateway 127.0.0.1:9000/shopping/coupon-template/template/create
     *
     * @param request template request object
     * @return coupon
     * @throws CouponException exception
     */
    @PostMapping("/template/create")
    public CouponTemplate createTemplate(@RequestBody TemplateRequest request)
            throws CouponException {
        log.info("Create Template: {}", JSON.toJSONString(request));
        return buildTemplateService.createTemplate(request);
    }

    /**
     * Coupon Template details
     * 127.0.0.1:7001/coupon-template/template/info?id=1
     * Access via gateway 127.0.0.1:9000/shopping/coupon-template/template/info?id=1
     *
     * @param id coupon template info
     * @return coupon template object
     * @throws CouponException exception
     */
    @GetMapping("/template/info")
    public CouponTemplate findCouponTemplateInfo(@RequestParam("id") Integer id)
            throws CouponException {
        log.info("Find Template info of: {}", id);
        return templateBaseService.findCouponTemplateInfo(id);
    }

    /**
     * Get all available coupon template
     * 127.0.0.1:7001/coupon-template/template/template/sdk/all
     * Access via gateway 127.0.0.1:9000/shopping/coupon-template/template/template/sdk/all
     *
     * @return coupon template sdk list
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findCouponTemplateInfo() {
        log.info("Find all available template");
        return templateBaseService.findAllAvailableTemplates();
    }

    /**
     * Get the mapping from ids to CouponTemplateSDK
     * 127.0.0.1:7001/coupon-template/template/sdk/all
     * Access via gateway 127.0.0.1:9000/shopping/coupon-template/template/sdk/all
     *
     * @return map<id, couponTemplateSDK>
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids) {
        log.info("Find Ids to template SDK", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
