package com.kaiyu.learning.videoprocessor;

import com.kaiyu.learning.domain.VideoDPI;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-08 09:30
 **/
public class VideoQuality720PStrategy  extends BaseVideoQualityStrategy implements VideoQualityStrategy{
    @Override
    public String processVideoQuality(String ffmpegPath, String sourceVideoPath, String targetVideoPath) {
        ConcurrentHashMap commandMap = new ConcurrentHashMap<String,String>();
        commandMap.put("-s","1280x720");
        commandMap.put("-b:a","192k");
        commandMap.put("-b:v","2500k");

        return getBaseProcessVideoQuality(ffmpegPath,sourceVideoPath,targetVideoPath, VideoDPI.DPI_720P.getPath(),commandMap);
    }
}
