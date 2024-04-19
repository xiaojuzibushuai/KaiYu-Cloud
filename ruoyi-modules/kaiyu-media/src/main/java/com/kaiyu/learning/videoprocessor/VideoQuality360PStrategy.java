package com.kaiyu.learning.videoprocessor;

import com.kaiyu.learning.domain.VideoDPI;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: kai-yu-cloud
 * @description: 360P
 * @author: xiaojuzi
 * @create: 2024-04-07 14:49
 **/
public class VideoQuality360PStrategy extends BaseVideoQualityStrategy implements VideoQualityStrategy  {
    @Override
    public String processVideoQuality(String ffmpegPath,String sourceVideoPath,String targetVideoPath) {
        ConcurrentHashMap commandMap = new ConcurrentHashMap<String,String>();

        return getBaseProcessVideoQuality(ffmpegPath,sourceVideoPath,targetVideoPath,VideoDPI.DPI_360P.getPath(),commandMap);
    }




}
