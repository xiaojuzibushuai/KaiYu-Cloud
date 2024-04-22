package com.kaiyu.media.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @program: kai-yu-cloud
 * @description: 阿里云OSS工具类
 * @author: xiaojuzi
 * @create: 2024-04-02 11:34
 **/

@Component
public class OssUtils {

    private static final Logger log = LoggerFactory.getLogger(OssUtils.class);

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    /**
     * 获取ossClient对象
     */
    public OSS createOSSClient(){
        // 创建OSSClient实例
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }


    /**
     * 简单上传文件
     * xiaojuzi
     * @param localFilePath 待上传的文件路径
     * @param objectName 上传后的对象路径
     * @param bucket OSS下载的桶
     * @return oss的访问路径（url）
     */
    public String simpleUploadFile(String objectName,String localFilePath,String bucket) {

        OSS ossClient  = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            //流式上传
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectName, new FileInputStream(localFilePath));
            putObjectRequest.setProcess("true");
            PutObjectResult result  = ossClient.putObject(putObjectRequest);
            if (result.getResponse().getStatusCode() == 200) {
                log.info("Uploaded file to OSS successfully.{}",result.getResponse().getUri());

                return result.getResponse().getUri();
            } else {
                log.error("Error uploading file to OSS: "+ result.getResponse().getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.debug("Error uploading file to OSS: " + e.getMessage());
            return null;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 重载方法 xiaojuzi
     * @param objectName
     * @param content 字节流
     * @param bucket OSS下载的桶
     * @return
     */
    public String simpleUploadFile(String objectName,ByteArrayInputStream content,String bucket) {

        OSS ossClient  = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            //流式上传
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectName, content);
            putObjectRequest.setProcess("true");
            PutObjectResult result  = ossClient.putObject(putObjectRequest);
            if (result.getResponse().getStatusCode() == 200) {
                log.info("Uploaded file to OSS successfully.");

                return result.getResponse().getUri();
            } else {
                log.error("Error uploading file to OSS: "+ result.getResponse().getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.debug("Error uploading file to OSS: " + e.getMessage());
            return null;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 简单下载文件
     * xiaojuzi
     * @param localFilePath 保存到本地文件夹
     * @param objectName OSS下载的对象路径
     * @param bucket OSS下载的桶
     */
    public Boolean simpleDownloadFile(String objectName,String localFilePath,String bucket) {

        OSS ossClient  = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {

            ossClient.getObject(new GetObjectRequest(bucket, objectName), new File(localFilePath));
            return true;

        } catch (Exception e) {
        log.debug("Error Downloading file from OSS: " + e.getMessage());
        return false;
    } finally {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    }


}
