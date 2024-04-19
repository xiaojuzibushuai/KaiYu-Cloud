package com.kaiyu.learning.domain;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-07 16:29
 **/

public enum VideoDPI {

    AUTO(0, "auto"),
    DPI_360P(1, "360p"),
    DPI_480P(2, "480p"),
    DPI_720P(3, "720p"),
    DPI_1080P(4, "1080p"),
    ORIGINAL(5, "original");


    private final int dpiValue;
    private final String path;

    VideoDPI(int dpiValue, String path) {
        this.dpiValue = dpiValue;
        this.path = path;
    }

    public int getDpiValue() {
        return dpiValue;
    }

    public String getPath() {
        return path;
    }

    public static VideoDPI getByDpiValue(int dpi) {
        for (VideoDPI videoDPI : values()) {
            if (videoDPI.getDpiValue() == dpi) {
                return videoDPI;
            }
        }
        return null;
    }
}