package com.ruoyi.common.core.exception;

import java.io.Serializable;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-03-29 11:18
 **/
public class RestErrorResponse implements Serializable {
    private String errMessage;

    public RestErrorResponse(String errMessage) {
        this.errMessage = errMessage;
    }
    public RestErrorResponse() {
    }

    public String getErrMessage() {
        return errMessage;
    }








}
