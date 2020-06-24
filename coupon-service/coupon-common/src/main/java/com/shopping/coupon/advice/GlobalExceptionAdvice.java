package com.shopping.coupon.advice;

import com.shopping.coupon.exception.CouponException;
import com.shopping.coupon.vo.CommonResponse;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * handle global exception
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     *  handle CouponException
     * */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest httpServletRequest,
                                                         CouponException ex) {
        CommonResponse<String> response = new CommonResponse<>(-1, "business error");
        response.setData(ex.getMessage());
        return response;
    }

    //TODO: add more exceptions to handle different cases using enum
}
