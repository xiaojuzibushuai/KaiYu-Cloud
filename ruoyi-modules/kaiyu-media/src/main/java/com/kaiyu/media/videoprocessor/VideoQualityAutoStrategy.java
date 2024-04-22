package com.kaiyu.media.videoprocessor;

import com.kaiyu.media.domain.VideoDPI;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-08 14:01
 **/
public class VideoQualityAutoStrategy extends BaseVideoQualityStrategy implements VideoQualityStrategy{
    @Override
    public String processVideoQuality(String ffmpegPath, String sourceVideoPath, String targetVideoPath) {
        ConcurrentHashMap commandMap = new ConcurrentHashMap<String,String>();
        commandMap.put("-preset","veryfast");
        commandMap.put("-tune","zerolatency");
        commandMap.put("-b:v","2000k");
        commandMap.put("-b:a","128k");
        commandMap.put("-vf","scale=1280:720");
        commandMap.put("-minrate:v","1000k");
        commandMap.put("-maxrate:v","4000k");
        commandMap.put("-bufsize:v","4000k");
        commandMap.put("-s","");
        commandMap.put("-pix_fmt","");
        commandMap.put("-r","");


        return getBaseProcessVideoQuality(ffmpegPath,sourceVideoPath,targetVideoPath, VideoDPI.AUTO.getPath(),commandMap);
    }
}
