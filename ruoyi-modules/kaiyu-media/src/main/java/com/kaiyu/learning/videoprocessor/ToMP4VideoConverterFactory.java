package com.kaiyu.learning.videoprocessor;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-07 14:47
 **/
public class ToMP4VideoConverterFactory extends VideoConverterFactory {

    @Override
    public VideoConverter createVideoConverter() {
        return new ToMP4VideoConverter();
    }
}
