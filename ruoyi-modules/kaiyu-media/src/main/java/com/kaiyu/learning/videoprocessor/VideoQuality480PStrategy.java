package com.kaiyu.learning.videoprocessor;

import com.kaiyu.learning.domain.VideoDPI;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-08 09:07
 **/
public class VideoQuality480PStrategy extends BaseVideoQualityStrategy implements VideoQualityStrategy{
    @Override
    public String processVideoQuality(String ffmpegPath, String sourceVideoPath, String targetVideoPath) {
        ConcurrentHashMap commandMap = new ConcurrentHashMap<String,String>();
        commandMap.put("-s","640x480");
        commandMap.put("-b:a","128k");
        commandMap.put("-b:v","1500k");

        return getBaseProcessVideoQuality(ffmpegPath,sourceVideoPath,targetVideoPath, VideoDPI.DPI_480P.getPath(),commandMap);
    }
}
