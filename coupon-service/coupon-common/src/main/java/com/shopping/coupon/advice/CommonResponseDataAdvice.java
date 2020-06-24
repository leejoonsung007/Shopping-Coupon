package com.shopping.coupon.advice;

import com.shopping.coupon.annotation.IgnoreResponseAdvice;
import com.shopping.coupon.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * process response body
 */
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    /**
     * determine whether to process the response
     * @param returnType
     * @param converterType
     * @return true/false
     */
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter, Class converterType) {
        // if current method of the class is marked as @IgnoreResponseAdvice, return false
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }

        // if current method is marked as @IgnoreResponseAdvice, return false
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }

        // call beforeBodyWrite if true
        return true;
    }

    /**
     * Process the data before write it to http response body
     * @param body
     * @param methodParameter
     * @param selectedContentType
     * @param selectedConverterType
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return response
     */
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter,
                                  MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        // define response which needs to response
        CommonResponse<Object> response = new CommonResponse<>(0, "");

        // if response body is null, no need to set data
        if (body == null) {
            return response;
        // If we defined the return data type of controller is CommonResponse, no need to precoess it
        } else if (body instanceof CommonResponse) {
            return (CommonResponse<Object>) body;
        } else {
            response.setData(body);
        }
        return response;
    }
}
