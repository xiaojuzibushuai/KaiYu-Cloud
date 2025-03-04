package com.kaiyu.order.domain;

import lombok.Data;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-01 15:19
 **/
@Data
public class RestResponse<T> {
    /**
     * 相应编码 0为正常 -1为错误
     */
    private int code;
    /**
     * 响应提示信息
     */
    private String msg;
    /**
     * 响应内容
     */
    private T data;

    public RestResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RestResponse() {
        this(200, "success");
    }

    /**
     * 错误信息的封装
     */
    public static <T> RestResponse<T> validfail() {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(-1);
        return response;
    }

    public static <T> RestResponse<T> validfail(String msg) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(-1);
        response.setMsg(msg);
        return response;
    }

    public static <T> RestResponse<T> validfail(String msg, T result) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(-1);
        response.setMsg(msg);
        response.setData(result);
        return response;
    }

    /**
     * 正常信息的封装
     */
    public static <T> RestResponse<T> success() {
        return new RestResponse<>();
    }

    public static <T> RestResponse<T> success(T result) {
        RestResponse<T> response = new RestResponse<>();
        response.setData(result);
        return response;
    }

    public static <T> RestResponse<T> success(String msg, T result) {
        RestResponse<T> response = new RestResponse<>();
        response.setMsg(msg);
        response.setData(result);
        return response;
    }
}
