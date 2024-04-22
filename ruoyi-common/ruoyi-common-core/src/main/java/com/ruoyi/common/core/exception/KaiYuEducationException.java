package com.ruoyi.common.core.exception;

/**
 * @program: kai-yu-cloud
 * @description: 开宇项目异常类
 * @author: xiaojuzi
 * @create: 2024-03-29 11:14
 **/
public class KaiYuEducationException extends RuntimeException {

    private String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    public KaiYuEducationException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public static void cast(CommonError commonError) {
        throw new KaiYuEducationException(commonError.getErrMessage());
    }

    public static void cast(String errMessage) {
        throw new KaiYuEducationException(errMessage);
    }
}
