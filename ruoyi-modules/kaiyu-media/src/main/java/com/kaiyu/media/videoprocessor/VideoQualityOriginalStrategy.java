package com.kaiyu.media.videoprocessor;

import com.kaiyu.media.domain.VideoDPI;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-08 09:33
 **/
public class VideoQualityOriginalStrategy  extends BaseVideoQualityStrategy implements VideoQualityStrategy{
    @Override
    public String processVideoQuality(String ffmpegPath, String sourceVideoPath, String targetVideoPath) {
        ConcurrentHashMap commandMap = new ConcurrentHashMap<String,String>();
        commandMap.put("-c:v","copy");
        commandMap.put("-c:a","copy");
        commandMap.put("-s","");
        commandMap.put("-pix_fmt","");
        commandMap.put("-b:a","");
        commandMap.put("-b:v","");
        commandMap.put("-r","");

        return getBaseProcessVideoQuality(ffmpegPath,sourceVideoPath,targetVideoPath, VideoDPI.ORIGINAL.getPath(),commandMap);
    }
}
