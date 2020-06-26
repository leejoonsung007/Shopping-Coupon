package com.shopping.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IKafkaService {

    /**
     * consume coupon kafaka message
     *
     * @param record consumer record
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);
}
