package com.kaiyu.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.kaiyu.learning.config.MqttProviderConfig;
import com.kaiyu.learning.domain.RestResponse;
import com.kaiyu.learning.domain.TeachplanMedia;
import com.kaiyu.learning.domain.dto.PushAnswerDto;
import com.kaiyu.learning.domain.dto.TeachplanDto;
import com.kaiyu.learning.feignclient.RemoteContentService;
import com.kaiyu.learning.feignclient.RemoteMediaService;
import com.kaiyu.learning.feignclient.RemoteSystemService;
import com.kaiyu.learning.service.LearningService;
import com.kaiyu.learning.util.LearningOssUtils;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.uuid.UUID;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.model.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-09 14:31
 **/
@Service
public class LearningServiceImpl implements LearningService {

    private static final Logger log = LoggerFactory.getLogger(LearningServiceImpl.class);

    @Autowired
//    @Qualifier("")
    RemoteContentService remoteContentService;

    @Autowired
//    @Qualifier("")
    RemoteMediaService remoteMediaService;

    @Autowired
    RemoteSystemService remoteSystemService;


    @Autowired
    private MqttProviderConfig providerClient;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LearningOssUtils learningOssUtils;

    @Override
    public RestResponse<Object> getVideo(Long courseId, String mediaId) {

        try {
            // 1. 远程调用内容管理服务，查询课程计划 业务设计待修改
            R<TeachplanDto> teachplanDtoR = remoteContentService.getTeachplanByCourseId(courseId);

            TeachplanDto teachplanDto = teachplanDtoR.getData();
            if (teachplanDto == null) {
                return RestResponse.validfail("课程计划不存在");
            }

            List<TeachplanMedia> teachplanMediaList = teachplanDto.getTeachplanMedia();
            if (teachplanMediaList == null || teachplanMediaList.isEmpty()) {
                return RestResponse.validfail("课程媒体不存在");
            }

            Optional<RestResponse<Object>> res = teachplanMediaList.stream()
                    .filter(teachplanMedia -> mediaId.equals(teachplanMedia.getMediaId()))
                    .findFirst()
                    .map(teachplanMedia -> {
                        // 2. 判断课程是否收费 业务设计待修改

                        // 3. 远程调用媒资管理服务，获取视频播放地址 业务设计待修改
                        return remoteMediaService.getPlayUrlByMediaId(mediaId);
                    });
            if (res.isPresent()) {
                if(res.get().getData()!=null){
                    return res.get();
                }else {
                    return RestResponse.validfail("课程媒体不存在");
                }
            }else {
                return RestResponse.validfail("课程媒体不存在");
            }

        } catch (Exception e) {
            // 异常处理，例如网络问题或服务调用失败
            return RestResponse.validfail("获取视频播放地址失败: " + e.getMessage());
        }
    }

    @Override
    public RestResponse<Object> videoAutoPushActionToDevice(String operate, String sceneid) {
        if (operate == null || sceneid == null) {
            return RestResponse.validfail("参数不合法");
        }

        List<LinkedHashMap<String, String>> result = (List<LinkedHashMap<String, String>>) remoteContentService.getDeviceListBySceneid(sceneid).getData();

        if (result == null || result.size() == 0) {
            return RestResponse.validfail("设备不存在");
        }


        result.forEach(device -> {
            Map<String, Object> pushJson = new HashMap<>();
            pushJson.put("type", "3");
            pushJson.put("deviceid", device.get("deviceid"));
            pushJson.put("fromuser", "");

            Map<String, Object> message = new HashMap<>();
            message.put("operate", operate);
            pushJson.put("message", message);

            //发送消息
            providerClient.publish(1,true,device.get("topic"), JSON.toJSONString(pushJson));

            log.info("对主题{},发送消息：{}",device.get("topic"),pushJson);

        });

        //没有ack机制暂时这样
        Map<String, Integer> send_result = new HashMap<>();
        send_result.put("success_send", result.size());
        send_result.put("errc_send", 0);
        send_result.put("send_devices_count", result.size());


        return RestResponse.success(send_result);
    }

    @Override
    public List getSceneList(HttpServletRequest request) {

        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            String phone = JwtUtils.getPhone(token);
            RestResponse<Object> sceneList = remoteContentService.getSceneList(phone);
            if (sceneList.getData() != null) {
                return (List) sceneList.getData();
            }
        }

        return null;
    }

    @Override
    public RestResponse<Object> pushAnswerToKeyBoard(PushAnswerDto pushAnswerDto) {

        List<LinkedHashMap<String, String>> result = (List<LinkedHashMap<String, String>>) remoteContentService.getExternalDeviceListBySceneid(pushAnswerDto.getSceneid()).getData();

        if (result == null || result.size() == 0) {
            return RestResponse.validfail("设备不存在");
        }

        if(StringUtils.isEmpty(pushAnswerDto.getAnswer())){
            pushAnswerDto.setAnswer("0");
        }


        result.forEach(device -> {
            String pushJson = "-"+pushAnswerDto.getParentid()+"-"+pushAnswerDto.getGametype()+pushAnswerDto.getAnswer()
                    +"-"+pushAnswerDto.getCourseid();

            //发送消息
            providerClient.publish(1,true,device.get("topic"),pushJson);

            log.info("对主题{},发送消息：{}",device.get("topic"),pushJson);

        });


        //没有ack机制暂时这样
        Map<String, Integer> send_result = new HashMap<>();
        send_result.put("success_send", result.size());
        send_result.put("errc_send", 0);
        send_result.put("send_devices_count", result.size());

        return RestResponse.success(send_result);
    }

    @Override
    public RestResponse<Object> videoAutoPushDatToDevice(String url, String sceneid) {
        if (url == null || sceneid == null) {
            return RestResponse.validfail("参数不合法");
        }
        //进行dat文件临时目录存放生成
        String s1 = null;
        String fileName = null;
        try {
            Path tempDirectory = Files.createTempDirectory(UUID.randomUUID().toString());
            String temp = tempDirectory.toString().replace("\\", "/");
            fileName = temp.substring(temp.lastIndexOf("/") + 1);

//            File tempDatFile = File.createTempFile(fileName, ".dat");
//            File tempLrcFile = File.createTempFile(fileName, ".lrc");

            //下载文件到本地
            boolean flag = getDownloadFileToLocal(url, temp, fileName);

            if (!flag) {
                return RestResponse.validfail("下载dat文件过程出错");
            }

            //将文件上传供下载访问
            String objectName1 = "dat/" +fileName +"/" +fileName + ".dat";
            String objectName2 = "dat/" +fileName +"/" +fileName + ".lrc";
            s1 = learningOssUtils.simpleUploadFile(objectName1,temp + "/" + fileName + ".dat","kaiyu-files-resource");
            String s2 = learningOssUtils.simpleUploadFile(objectName2,temp + "/" + fileName + ".lrc","kaiyu-files-resource");
            if(s1 == null || s2 == null){
                return RestResponse.validfail("上传dat或lrc文件过程出错");
            }

        }catch (IOException e) {
            KaiYuEducationException.cast("下载dat文件过程出错:" + e.getMessage());
        }

        //获取场景下的设备
        List<LinkedHashMap<String, String>> result = (List<LinkedHashMap<String, String>>) remoteContentService.getDeviceListBySceneid(sceneid).getData();

        if (result == null || result.size() == 0) {
            return RestResponse.validfail("设备不存在");
        }

        String sendFileName = fileName;
        String sendUrl = s1.substring(0,s1.lastIndexOf("/"));

        result.forEach(device -> {
            Map<String, Object> pushJson = new HashMap<>();
            pushJson.put("type", "2");
            pushJson.put("deviceid", device.get("deviceid"));
            pushJson.put("fromuser", "");

            Map<String, Object> message = new HashMap<>();
            message.put("arg", sendFileName);
            message.put("url", sendUrl);

            pushJson.put("message", message);

            //发送消息
            providerClient.publish(1,true,device.get("topic"), JSON.toJSONString(pushJson));

            log.info("对主题{},发送消息：{}",device.get("topic"),pushJson);

        });

        //没有ack机制暂时这样
        Map<String, Integer> send_result = new HashMap<>();
        send_result.put("success_send", result.size());
        send_result.put("errc_send", 0);
        send_result.put("send_devices_count", result.size());


        return RestResponse.success(send_result);
    }


    public boolean getDownloadFileToLocal(String url, String fileDirectory, String fileName) {
        try {
            // 使用 URLDecoder 解码 URL
            String decodedUrl = URLDecoder.decode(url, "UTF-8");
            // 发送请求
            ResponseEntity<byte[]> exchange = restTemplate.exchange(decodedUrl, HttpMethod.GET, null, byte[].class);
            byte[] body = exchange.getBody();
            HttpStatus statusCode = exchange.getStatusCode();
            if (statusCode == HttpStatus.OK && body != null ) {
                //写入本地
                File datFile = new File(fileDirectory, fileName + ".dat");
                try(FileOutputStream fos = new FileOutputStream(datFile)) {
                    fos.write(body);
                }
                // 创建并写入 .lrc 文件
                File lrcFile = new File(fileDirectory, fileName + ".lrc");
                try (FileOutputStream fos = new FileOutputStream(lrcFile)) {
                    fos.write("000000000000000000000000000000000".getBytes());
                }
                return true;
            }

            // 请求失败
            log.info("getDownloadFileToLocal下载失败，body为空或状态码有问题: {0}", statusCode);
            return false;

        } catch (Exception e) {
            log.info("getDownloadFileToLocal下载失败，错误信息: {0}", e.getMessage());
            return false;
        }
    }


}
