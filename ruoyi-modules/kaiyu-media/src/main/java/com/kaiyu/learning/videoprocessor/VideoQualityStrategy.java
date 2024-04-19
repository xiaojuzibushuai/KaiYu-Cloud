package com.kaiyu.learning.videoprocessor;

/**
 * @program: kai-yu-cloud
 * @description: 视频清晰度策略接口
 * @author: xiaojuzi
 * @create: 2024-04-07 14:45
 **/
public interface VideoQualityStrategy {
    String processVideoQuality(String ffmpegPath,String sourceVideoPath,String targetVideoPath);
}
