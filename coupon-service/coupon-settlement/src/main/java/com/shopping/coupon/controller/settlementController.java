package com.shopping.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.service.ExecutorManager;
import com.shopping.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class settlementController {

    private ExecutorManager executorManager;

    @Autowired
    public settlementController(ExecutorManager executorManager) {
        this.executorManager = executorManager;
    }

    /**
     * Apply coupon
     * 127.0.0.1:port?/coupon-settlement/settlement/compute
     * @param settlementInfo settlementInfo passed by userd
     * @return modified settlementInfo
     * @throws CouponException exception
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlementInfo) throws CouponException {
        log.info("Settlement: {}", JSON.toJSONString(settlementInfo));
        return executorManager.computerRule(settlementInfo);
    }
 }
