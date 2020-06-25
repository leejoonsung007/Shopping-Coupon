package com.shopping.coupon.constant;

/**
 * Constant
 */
public class Constant {

    //Kafka message's topic
    public static final String TOPIC = "user_coupon_op";

    public static class RedisPrefix {
        public static final String COUPON_TEMPLATE = "coupon_template_code_";

        public static final String USER_AVAILABLE_COUPON = "user_available_coupon_";

        public static final String USER_USED_COUPON = "user_used_coupon";

        public static final String USER_EXPIRED_COUPON = "user_expired_coupon";
    }
}
