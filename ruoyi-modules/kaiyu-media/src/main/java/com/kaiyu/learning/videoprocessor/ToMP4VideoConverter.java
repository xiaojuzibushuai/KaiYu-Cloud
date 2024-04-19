package com.kaiyu.learning.videoprocessor;

import com.kaiyu.learning.util.Mp4VideoUtil;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-07 14:48
 **/

public class ToMP4VideoConverter implements VideoConverter{


    @Override
    public String convert(String ffmpegPath, String sourceVideoPath, String targetVideoPath) {
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpegPath, sourceVideoPath, targetVideoPath);
        String result = mp4VideoUtil.generateMp4();
        return result;
    }

}
