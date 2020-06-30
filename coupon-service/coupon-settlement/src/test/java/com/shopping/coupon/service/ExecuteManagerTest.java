package com.shopping.coupon.service;

import com.alibaba.fastjson.JSON;
import com.shopping.coupon.SettlementApplicationTests;
import com.shopping.coupon.constant.CouponCategory;
import com.shopping.coupon.constant.ProductType;
import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.vo.CouponTemplateSDK;
import com.shopping.coupon.vo.ProductInfo;
import com.shopping.coupon.vo.SettlementInfo;
import com.shopping.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
public class ExecuteManagerTest extends SettlementApplicationTests {

    private final Long USER_ID = 200001L;

    @Autowired
    private ExecutorManager manager;

    @Test
    public void ComputerRule() throws CouponException {
        // OFF coupon
//        log.info("OFF Coupon Executor Test");
//        SettlementInfo offInfo = createOffCouponSettlement();
//        SettlementInfo offResult = manager.computerRule(offInfo);
//
//        log.info("{}", offResult.getCost());
//        log.info("{}", offResult.getCouponAndTemplateInfos().size());
//        log.info("{}", offResult.getCouponAndTemplateInfos());

        // DISCOUNT coupon
//        log.info("OFF Coupon Executor Test");
//        SettlementInfo discountInfo = createDiscountCouponSettlement();
//        SettlementInfo discountResult = manager.computerRule(discountInfo);
//
//        log.info("{}", discountResult.getCost());
//        log.info("{}", discountResult.getCouponAndTemplateInfos().size());
//        log.info("{}", discountResult.getCouponAndTemplateInfos());

        // CASH BACK coupon
//        log.info("OFF Coupon Executor Test");
//        SettlementInfo cashBackInfo = createCashBackCouponSettlement();
//        SettlementInfo cashBackResult = manager.computerRule(cashBackInfo);
//
//        log.info("{}", cashBackResult.getCost());
//        log.info("{}", cashBackResult.getCouponAndTemplateInfos().size());
//        log.info("{}", cashBackResult.getCouponAndTemplateInfos());

        // OFF and DISCOUNT coupon
        log.info("off and discount Coupon Executor Test");
        SettlementInfo offDiscountInfo = createOffDiscountCouponSettlement();
        SettlementInfo offDiscountResult = manager.computerRule(offDiscountInfo);

        log.info("{}", offDiscountResult.getCost());
        log.info("{}", offDiscountResult.getCouponAndTemplateInfos().size());
        log.info("{}", offDiscountResult.getCouponAndTemplateInfos());

    }

    private SettlementInfo createOffCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(USER_ID);
        info.setPaid(false);
        info.setCost(0.0);

        ProductInfo productInfo1 = new ProductInfo();
        productInfo1.setCount(2);
        productInfo1.setPrice(29.9);
        productInfo1.setType(ProductType.FOOD.getCode());

        ProductInfo productInfo2 = new ProductInfo();
        productInfo2.setCount(2);
        productInfo2.setPrice(39.9);
        productInfo2.setType(ProductType.HOME.getCode());

        info.setProductInfos(Arrays.asList(productInfo1, productInfo2));

        SettlementInfo.CouponAndTemplateInfo couponAndTemplateInfo = new SettlementInfo.CouponAndTemplateInfo();
        couponAndTemplateInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.OFF.getCode());
        templateSDK.setKey("100120200630");

        TemplateRule templateRule = new TemplateRule();
        templateRule.setDiscount(new TemplateRule.Discount(20, 50));
        templateRule.setUsage(new TemplateRule.Usage("Dublin", "Leister",
                JSON.toJSONString(Arrays.asList(ProductType.FOOD.getCode(), ProductType.HOME.getCode()))));
        templateSDK.setRule(templateRule);

        couponAndTemplateInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(couponAndTemplateInfo));

        return info;
    }

    private SettlementInfo createDiscountCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(USER_ID);
        info.setPaid(false);
        info.setCost(0.0);

        ProductInfo productInfo1 = new ProductInfo();
        productInfo1.setCount(2);
        productInfo1.setPrice(29.9);
        productInfo1.setType(ProductType.FOOD.getCode());

        ProductInfo productInfo2 = new ProductInfo();
        productInfo2.setCount(2);
        productInfo2.setPrice(39.9);
        productInfo2.setType(ProductType.FOOD.getCode());

        info.setProductInfos(Arrays.asList(productInfo1, productInfo2));

        SettlementInfo.CouponAndTemplateInfo couponAndTemplateInfo = new SettlementInfo.CouponAndTemplateInfo();
        couponAndTemplateInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.DISCOUNT.getCode());
        templateSDK.setKey("100120200630");

        TemplateRule templateRule = new TemplateRule();
        templateRule.setDiscount(new TemplateRule.Discount(85,20));
        templateRule.setUsage(new TemplateRule.Usage("Dublin", "Leister",
                JSON.toJSONString(Arrays.asList(ProductType.STATIONERY.getCode(), ProductType.HOME.getCode()))));
        templateSDK.setRule(templateRule);

        couponAndTemplateInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(couponAndTemplateInfo));

        return info;
    }

    private SettlementInfo createCashBackCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(USER_ID);
        info.setPaid(false);
        info.setCost(0.0);

        ProductInfo productInfo1 = new ProductInfo();
        productInfo1.setCount(2);
        productInfo1.setPrice(29.9);
        productInfo1.setType(ProductType.FOOD.getCode());

        ProductInfo productInfo2 = new ProductInfo();
        productInfo2.setCount(2);
        productInfo2.setPrice(39.9);
        productInfo2.setType(ProductType.HOME.getCode());

        info.setProductInfos(Arrays.asList(productInfo1, productInfo2));

        SettlementInfo.CouponAndTemplateInfo couponAndTemplateInfo = new SettlementInfo.CouponAndTemplateInfo();
        couponAndTemplateInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.CASH_BACK.getCode());
        templateSDK.setKey("100120200630");

        TemplateRule templateRule = new TemplateRule();
        templateRule.setDiscount(new TemplateRule.Discount(50,20));
        templateRule.setUsage(new TemplateRule.Usage("Dublin", "Leister",
                JSON.toJSONString(Arrays.asList(ProductType.FOOD.getCode(), ProductType.HOME.getCode()))));
        templateSDK.setRule(templateRule);

        couponAndTemplateInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(couponAndTemplateInfo));

        return info;
    }

    private SettlementInfo createOffDiscountCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(USER_ID);
        info.setPaid(false);
        info.setCost(0.0);

        ProductInfo productInfo1 = new ProductInfo();
        productInfo1.setCount(2);
        productInfo1.setPrice(29.9);
        productInfo1.setType(ProductType.FOOD.getCode());

        ProductInfo productInfo2 = new ProductInfo();
        productInfo2.setCount(2);
        productInfo2.setPrice(39.9);
        productInfo2.setType(ProductType.HOME.getCode());

        info.setProductInfos(Arrays.asList(productInfo1, productInfo2));

        // off coupon
        SettlementInfo.CouponAndTemplateInfo offInfo = new SettlementInfo.CouponAndTemplateInfo();
        offInfo.setId(1);

        CouponTemplateSDK offTemplateSDK = new CouponTemplateSDK();
        offTemplateSDK.setId(1);
        offTemplateSDK.setCategory(CouponCategory.OFF.getCode());
        offTemplateSDK.setKey("100120200630");

        TemplateRule offTemplateRule = new TemplateRule();
        offTemplateRule.setDiscount(new TemplateRule.Discount(50,100));
        offTemplateRule.setUsage(new TemplateRule.Usage("Leister", "Dublin",
                JSON.toJSONString(Arrays.asList(ProductType.FOOD.getCode(), ProductType.HOME.getCode()))));
        offTemplateRule.setWeight(JSON.toJSONString(Collections.emptyList()));
        offTemplateSDK.setRule(offTemplateRule);
        offInfo.setTemplate(offTemplateSDK);



        // discount coupon
        SettlementInfo.CouponAndTemplateInfo discountInfo = new SettlementInfo.CouponAndTemplateInfo();
        discountInfo.setId(2);

        CouponTemplateSDK discountTemplateSDK = new CouponTemplateSDK();
        discountTemplateSDK.setId(2);
        discountTemplateSDK.setCategory(CouponCategory.DISCOUNT.getCode());
        discountTemplateSDK.setKey("100120200630");

        TemplateRule discountTemplateRule = new TemplateRule();
        discountTemplateRule.setDiscount(new TemplateRule.Discount(85,1));
        discountTemplateRule.setUsage(new TemplateRule.Usage("Leister", "Dublin",
                JSON.toJSONString(Arrays.asList(ProductType.FOOD.getCode(), ProductType.HOME.getCode()))));
        discountTemplateSDK.setRule(discountTemplateRule);
        discountInfo.setTemplate(discountTemplateSDK);

        discountTemplateRule.setWeight(JSON.toJSONString(Collections.singletonList("1001202006300001")));
        info.setCouponAndTemplateInfos(Arrays.asList(offInfo, discountInfo));

        return info;
    }
}
