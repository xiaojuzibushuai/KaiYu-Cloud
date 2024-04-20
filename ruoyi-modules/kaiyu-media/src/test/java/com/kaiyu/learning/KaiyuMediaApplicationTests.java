package com.kaiyu.learning;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.kaiyu.learning.util.OssUtils;
import com.kaiyu.learning.videoprocessor.ToMP4VideoConverterFactory;
import com.kaiyu.learning.videoprocessor.VideoConverter;
import com.kaiyu.learning.videoprocessor.VideoConverterFactory;
import com.ruoyi.common.core.utils.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KaiyuMediaApplicationTests {

    @Autowired
    OssUtils ossUtils;

    @Value("${aliyun.oss.files.bucketName}")
    private String bucket_files;

    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegPath;

    @Test
    void testToMP4(){
        // 使用工厂模式创建视频转换工厂
        VideoConverterFactory factory = new ToMP4VideoConverterFactory();
        VideoConverter converter = factory.createVideoConverter();
        // 转换视频格式
        converter.convert(ffmpegPath,"D:\\桌面\\test1.avi","D:\\桌面\\targetVideo.mp4");

    }

    @Test
    void test1(){
        String s = "5/d/5d3d3277f89ffe00f5d3074eca703d34/5d3d3277f89ffe00f5d3074eca703d34.mp4";
        System.out.println(s.substring(0,s.lastIndexOf("/")));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("video_base_url", "http://kaiyu-video-resource.oss-cn-wuhan-lr.aliyuncs.com/5/d/5d3d3277f89ffe00f5d3074eca703d34/original");
        jsonObject.put("video_key_url", "http://kaiyu-video-resource.oss-cn-wuhan-lr.aliyuncs.com/5/5d3d3277f89ffe00f5d3074eca703d34/original");
        jsonObject.put("video_ts_list", 5);
        jsonObject.put("episode", "1");
        jsonObject.put("dpi", "5");

        String url = jsonObject.toJSONString();
        System.out.println(url);

        JSONArray jsonArray = new JSONArray();

        jsonArray.add(JSON.parseObject(url));

        String saveUrl = jsonArray.toJSONString();

        System.out.println(saveUrl);

        if (StringUtils.isNotEmpty(saveUrl)){
            JSONArray jsonArray1 = JSONArray.parseArray(saveUrl);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("video_base_url", "http://kaiyu-video-resource.oss-cn-wuhan-lr.aliyuncs.com/5/d/5d3d3277f89ffe00f5d3074eca703d34/original");
            jsonObject1.put("video_key_url", "http://kaiyu-video-resource.oss-cn-wuhan-lr.aliyuncs.com/5/5d3d3277f89ffe00f5d3074eca703d34/original");
            jsonObject1.put("video_ts_list", 10);
            jsonObject1.put("episode", "2");
            jsonObject1.put("dpi", "1");

            jsonArray1.add(jsonObject1);

            System.out.println(jsonArray1.toJSONString());
        }



    }

    @Test
    void test2(){
        String s ="5/d/5d3d3277f89ffe00f5d3074eca703d34/chunk/0";
        String[] parts = s.split("/");
        String lastPart = parts[parts.length - 1];
//        String numString = lastPart.replaceAll("\\D+", "");
        String numString = lastPart.trim();
        System.out.println(Long.parseLong(numString));
    }


    @Test
    void testUploadFile(){
        String s = ossUtils.simpleUploadFile("mdbx.dat", "D:\\mdbx.dat",bucket_files);
        System.out.println(s);
    }

    @Test
    void testDownloadFile(){
        ossUtils.simpleDownloadFile("mdbx.dat","E:\\mdbx.dat",bucket_files);
    }

}
