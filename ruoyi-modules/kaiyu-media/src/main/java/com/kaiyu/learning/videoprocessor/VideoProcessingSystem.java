package com.kaiyu.learning.videoprocessor;

import com.kaiyu.learning.domain.VideoQuality;

/**
 * @program: kai-yu-cloud
 * @description: 视频处理 测试类
 * @author: xiaojuzi
 * @create: 2024-04-07 14:49
 **/
public class VideoProcessingSystem {

    public static void main(String[] args) {
        // 使用工厂模式创建视频转换工厂
        VideoConverterFactory factory = new ToMP4VideoConverterFactory();
        VideoConverter converter = factory.createVideoConverter();
        // 转换视频格式
//        converter.convert("D:\\桌面\\ffmpeg\\ffmpeg.exe","D:\\桌面\\test1.avi","D:\\桌面\\targetVideo.mp4");

        // 使用策略模式处理不同清晰度视频
        VideoQualityStrategy strategy360P = new VideoQuality360PStrategy();
        VideoQualityOriginalStrategy originalStrategy = new VideoQualityOriginalStrategy();
        VideoQuality1080PStrategy Strategy1080P = new VideoQuality1080PStrategy();
        VideoQualityStrategy videoQualityStrategy = null;
        try {
            videoQualityStrategy = VideoQuality.getStrategyClassByCode("4").
                    getDeclaredConstructor().newInstance();
            // 处理不同清晰度视频
            String result = videoQualityStrategy.processVideoQuality("D:\\桌面\\ffmpeg\\ffmpeg.exe", "E:\\drawbo_v2\\static\\video\\5\\d\\5d3d3277f89ffe00f5d3074eca703d35\\1080P\\1.mp4",
                    "E:\\drawbo_v2\\static\\video\\5\\d\\5d3d3277f89ffe00f5d3074eca703d35\\1080P\\2.mp4");

            String cleanedString = result.substring(1, result.length() - 1);
            for (String filePath : cleanedString.split(", ")){
                System.out.println(filePath);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

//        String  s = "E:/drawbo_v2/static/video/5/d/5d3d3277f89ffe00f5d3074eca703d35/1080P/encrypted_slice.m3u8";
//        // 寻找最后一个反斜杠的位置
//        int lastIndex = s.lastIndexOf("/");
//
//        // 截取路径部分
//        String desiredPath = s.substring(0,lastIndex);
//        String[] pathParts  = s.split("/");
//        StringBuffer sb1 = new StringBuffer();
//        for (int i = pathParts.length-4; i < pathParts.length; i++) { // 从第6个部分开始拼接
//            sb1.append(pathParts[i]);
//            if (i < pathParts.length - 1) {
//                sb1.append("/");
//            }
//        }
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("video_base_url", desiredPath);
//        jsonObject.put("video_key_url", desiredPath);
//        System.out.println(jsonObject.toJSONString());
//        System.out.println(desiredPath);


    }
}
