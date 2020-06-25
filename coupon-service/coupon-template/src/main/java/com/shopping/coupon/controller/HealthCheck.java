package com.shopping.coupon.controller;

import com.shopping.coupon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Slf4j
@RestController
public class HealthCheck {

    private DiscoveryClient client;

    private Registration registration;

    @Autowired
    public HealthCheck(DiscoveryClient client, Registration registration) {
        this.client = client;
        this.registration = registration;
    }

    /**
     * Service health check API
     * 127.0.0.1:7001/coupon-template/health
     * @return string
     */
    @GetMapping("/health")
    public String health() {
        log.debug("view health api");
        return "Coupon Template is OK";
    }

    /**
     * Exception check API
     * 127.0.0.1:7001/coupon-template/exception
     * @return string
     */
    @GetMapping("/exception")
    public String exception() throws CouponException {

        log.debug("view exception api");
        throw new CouponException("Coupon Template encounter an problem");
    }

    /**
     * Eureka Server microservices info
     * 127.0.0.1:7001/coupon-template/exception
     * @return string
     */
    @GetMapping("/info")
    public List<Map<String, Object>> info() {
        List<ServiceInstance> instances = client.getInstances(registration.getServiceId());

        List<Map<String, Object>> result = new ArrayList<>();

        instances.forEach(instance -> {
            Map<String, Object> info = new HashMap<>();
            info.put("serviceId", instance.getServiceId());
            info.put("instanceId", instance.getInstanceId());
            info.put("port", instance.getPort());
            info.put("uri", instance.getUri());
            info.put("host", instance.getHost());

            result.add(info);
        });

        return result;
    }
}
