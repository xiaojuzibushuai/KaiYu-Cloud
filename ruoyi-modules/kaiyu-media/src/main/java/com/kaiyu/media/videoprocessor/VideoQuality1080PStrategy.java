package com.kaiyu.media.videoprocessor;

import com.kaiyu.media.domain.VideoDPI;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-08 09:31
 **/
public class VideoQuality1080PStrategy extends BaseVideoQualityStrategy implements VideoQualityStrategy{
    @Override
    public String processVideoQuality(String ffmpegPath, String sourceVideoPath, String targetVideoPath) {
        ConcurrentHashMap commandMap = new ConcurrentHashMap<String,String>();
        commandMap.put("-s","1920x1080");
        commandMap.put("-b:a","192k");
        commandMap.put("-b:v","5000k");

        return getBaseProcessVideoQuality(ffmpegPath,sourceVideoPath,targetVideoPath, VideoDPI.DPI_1080P.getPath(),commandMap);
    }
}
