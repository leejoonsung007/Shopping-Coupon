package com.shopping.coupon.service;

import com.shopping.coupon.constant.CouponCategory;
import com.shopping.coupon.constant.RuleFlag;
import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Coupon Settlement Executor manager
 * Find the corresponding executor (by settlementInfo passed by user) to compute the price
 * BeanPostProcessor: After all beans are created by spring, this class and methods will be called
 */
@Slf4j
@Component
public class ExecutorManager implements BeanPostProcessor {

    private static Map<RuleFlag, RuleExecutor> executorMap = new HashMap<>(RuleFlag.values().length);

    /**
     * Coupon computation rule entry
     * NB: The number of Coupon must be greater or equal to 1
     * @param settlementInfo settlementInfo
     * @return settlementIndo
     * @throws CouponException
     */
    public SettlementInfo computerRule(SettlementInfo settlementInfo) throws CouponException {
        SettlementInfo result = null;

        // single coupon
        if (settlementInfo.getCouponAndTemplateInfos().size() == 1) {
            CouponCategory category = CouponCategory.of(settlementInfo.getCouponAndTemplateInfos()
                    .get(0).getTemplate().getCategory());

            switch(category) {
                case OFF:
                    result = executorMap.get(RuleFlag.OFF).computeRule(settlementInfo);
                    break;
                case DISCOUNT:
                    result = executorMap.get(RuleFlag.DISCOUNT).computeRule(settlementInfo);
                    break;
                case CASH_BACK:
                    result = executorMap.get(RuleFlag.CASH_BACK).computeRule(settlementInfo);
                    break;
            }
        } else {

            // stacked coupon
            List<CouponCategory> categories = new ArrayList<>(settlementInfo.getCouponAndTemplateInfos().size());
            settlementInfo.getCouponAndTemplateInfos().forEach(info -> categories.add(
                    CouponCategory.of(info.getTemplate().getCategory())));
            if (categories.size() != 2) {
                throw new CouponException("Not allowed to use more than two coupons");
            } else {
                if (categories.contains(CouponCategory.OFF) && categories.contains(CouponCategory.DISCOUNT)) {
                    result = executorMap.get(RuleFlag.OFF_DISCOUNT).computeRule(settlementInfo);
                } else {
                    throw new CouponException("This set of coupons are not allowed");
                }
            }
        }
        return result;
    }

    /**
     * Called before bean initialization
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (!(bean instanceof RuleExecutor)) {
            return bean;
        }

        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlag ruleFlag = executor.ruleConfig();

        if (executorMap.containsKey(ruleFlag)) {
            throw new IllegalStateException("The executor for rule flag is already existed: " + ruleFlag);
        }

        log.info("Load executor {} for rule flag {}", executor.getClass(), ruleFlag);
        executorMap.put(ruleFlag, executor);
        return null;
    }

    /**
     * Called after bean initialization
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
