package com.kaiyu.media.domain;

import com.kaiyu.media.videoprocessor.*;

/**
 * @program: kai-yu-cloud
 * @description: 视频质量
 * @author: xiaojuzi
 * @create: 2024-04-08 14:12
 **/
public enum VideoQuality {
    AUTO("0", VideoQualityAutoStrategy.class),
    QUALITY_360P("1", VideoQuality360PStrategy.class),
    QUALITY_480P("2", VideoQuality480PStrategy.class),
    QUALITY_720P("3", VideoQuality720PStrategy.class),
    QUALITY_1080P("4", VideoQuality1080PStrategy.class),
    ORIGINAL("5", VideoQualityOriginalStrategy.class);

    private final String code;
    private final Class<? extends VideoQualityStrategy> strategyClass;

    VideoQuality(String code, Class<? extends VideoQualityStrategy> strategyClass) {
        this.code = code;
        this.strategyClass = strategyClass;
    }

    // 根据code获取对应的策略类
    public static Class<? extends VideoQualityStrategy> getStrategyClassByCode(String code) {
        for (VideoQuality quality : values()) {
            if (quality.code.equals(code)) {
                return quality.strategyClass;
            }
        }
        return VideoQuality.ORIGINAL.strategyClass;
    }

}

