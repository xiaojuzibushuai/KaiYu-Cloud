package com.kaiyu.learning.videoprocessor;

/**
 * @program: kai-yu-cloud
 * @description: 视频转换器接口
 * @author: xiaojuzi
 * @create: 2024-04-07 14:43
 **/
public interface VideoConverter {

    public String convert(String ffmpegPath,String sourceVideoPath,String targetVideoPath);

}
