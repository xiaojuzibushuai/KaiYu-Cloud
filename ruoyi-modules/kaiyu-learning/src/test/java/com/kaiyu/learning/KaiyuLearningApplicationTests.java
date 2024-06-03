package com.kaiyu.learning;

import com.kaiyu.learning.util.LearningOssUtils;
import com.ruoyi.common.core.utils.uuid.UUID;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
@MapperScan({"com.kaiyu.learning.mapper"})
class KaiyuLearningApplicationTests {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LearningOssUtils learningOssUtils;


    @Test
    void contextLoads() {
    }

    @Test
    void test(){
        String url = "http://kaiyu-image-resource.oss-cn-hangzhou.aliyuncs.com/images/e56f2428-ce30-401e-8c5f-99a255fc0339-%E6%B8%94%E6%AD%8C%E5%AD%90%2001.dat";
//        System.out.println(url.substring(url.lastIndexOf("/")+1));
        // 使用 UriComponentsBuilder 进行 URL 编码
//        String encodedUrl = UriComponentsBuilder.fromHttpUrl(url).build().encode().toUriString();
        try {
            Path tempDirectory = Files.createTempDirectory(UUID.randomUUID().toString());
            String temp = tempDirectory.toString().replace("\\", "/");
            String fileName = temp.substring(temp.lastIndexOf("/") + 1);

//            File tempDatFile = File.createTempFile(fileName, ".dat");
//            File tempLrcFile = File.createTempFile(fileName, ".lrc");

            // 使用 URLDecoder 解码 URL
            String decodedUrl = URLDecoder.decode(url, "UTF-8");
            // 发送请求
            ResponseEntity<byte[]> exchange = restTemplate.exchange(decodedUrl, HttpMethod.GET, null, byte[].class);
            byte[] body = exchange.getBody();
            HttpStatus statusCode = exchange.getStatusCode();

            //写入本地
            File datFile = new File(temp, fileName + ".dat");
            try(FileOutputStream fos = new FileOutputStream(datFile)) {
                fos.write(body);
            }
            // 创建并写入 .lrc 文件
            File lrcFile = new File(temp, fileName + ".lrc");
            try (FileOutputStream fos = new FileOutputStream(lrcFile)) {
                fos.write("00000000".getBytes());
            }

            System.out.println("下载成功");

            //将文件上传供下载访问
            String objectName1 = "dat/" +fileName +"/" +fileName + ".dat";
            String objectName2 = "dat/" +fileName +"/" +fileName + ".lrc";
            String s1 = learningOssUtils.simpleUploadFile(objectName1,temp + "/" + fileName + ".dat","kaiyu-files-resource");
            String s2 = learningOssUtils.simpleUploadFile(objectName2,temp + "/" + fileName + ".lrc","kaiyu-files-resource");
            System.out.println(s1.substring(0,s1.lastIndexOf("/")));
            System.out.println(s1);
            System.out.println(s2);

//        System.out.println(body);
//        System.out.println(statusCode);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
