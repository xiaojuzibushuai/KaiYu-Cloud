package com.ruoyi.common.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @program: kai-yu-cloud
 * @description:全局异常处理器
 * @author: xiaojuzi
 * @create: 2024-03-29 11:17
 **/

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(KaiYuEducationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 该异常枚举错误码为500，
    public RestErrorResponse customException(KaiYuEducationException exception) {

        exception.printStackTrace();

        return new RestErrorResponse(exception.getErrMessage());
    }


}
