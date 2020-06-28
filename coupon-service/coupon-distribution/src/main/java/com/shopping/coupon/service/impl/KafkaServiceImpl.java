package com.shopping.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.shopping.coupon.constant.Constant;
import com.shopping.coupon.constant.CouponStatus;
import com.shopping.coupon.dao.CouponDao;
import com.shopping.coupon.entity.Coupon;
import com.shopping.coupon.service.IKafkaService;
import com.shopping.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;

/**
 * Implementation of Kafka Service
 * Sync the state of coupon to DB
 */
@Slf4j
@Component
public class KafkaServiceImpl implements IKafkaService {

    private CouponDao couponDao;

    @Autowired
    public KafkaServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    /**
     * Kafka will call this function
     * @param record consumer record
     */
    @Override
    @KafkaListener(topics={Constant.TOPIC}, groupId="coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponKafkaMessage = JSON.parseObject(message.toString(),
                    CouponKafkaMessage.class);
            log.info("Receive CouponKafkaMessage:{}", message.toString());

            CouponStatus status = CouponStatus.of(couponKafkaMessage.getStatus());

            switch(status) {
                case AVAILABLE:
                    break;
                case USED:
                    processUsedCoupon(couponKafkaMessage, status);
                    break;
                case EXPIRED:
                    processExpiredCoupon(couponKafkaMessage, status);
                    break;
            }
        }
    }

    private void processUsedCoupon(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        //Todo send message to user
        processCouponByStatus(kafkaMessage, status);
    }

    private void processExpiredCoupon(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        //Todo send notification to user
        processCouponByStatus(kafkaMessage, status);
    }

    private void processCouponByStatus(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        List<Coupon> coupons = couponDao.findAllById(kafkaMessage.getIds());
        if (CollectionUtils.isEmpty(coupons) || coupons.size() != kafkaMessage.getIds().size()) {
            log.error("Cannot Find The Correct Coupon info:{}", JSON.toJSONString(kafkaMessage));
            //TODO send message/email
        }

        coupons.forEach(coupon -> coupon.setStatus(status));
        log.info("CouponKafkaMessage OP Coupon Count:{}", couponDao.saveAll(coupons).size());
    }
}
