package com.kaiyu.media.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.kaiyu.media.domain.MediaProcess;
import com.kaiyu.media.domain.VideoQuality;
import com.kaiyu.media.service.MediaFileProcessService;
import com.kaiyu.media.service.MediaFileService;
import com.kaiyu.media.util.CustomVideoUtil;
import com.kaiyu.media.util.OssUtils;
import com.kaiyu.media.videoprocessor.VideoQualityStrategy;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.uuid.UUID;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.*;

/**
 * @program: kai-yu-cloud
 * @description: 视频处理任务
 * @author: xiaojuzi
 * @create: 2024-04-03 11:53
 **/
@Component
public class VideoTask {

    private static final Logger log = LoggerFactory.getLogger(VideoTask.class);

    @Value("${videoprocess.ffmpegpath}")
    String ffmpegPath;

    @Autowired
    private MediaFileService mediaFileService;

    @Autowired
    private OssUtils ossUtils;

    @Autowired
    private MediaFileProcessService mediaFileProcessService;

    /**
     * 分片广播 任务调度 视频处理 xiaojuzi
     * 并发处理视频 异步处理
     *
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandlerAsync() {

        // 分片序号
        int shardIndex = XxlJobHelper.getShardIndex();
        // 分片总数
        int shardTotal = XxlJobHelper.getShardTotal();

        log.debug("========== shardIndex:{}, shardTotal:{} =========", shardIndex, shardTotal);

        List<MediaProcess> mediaProcessList = null;
        int size = 0;

        try {
            // 查询cpu核心数
            int corePoolSize = Runtime.getRuntime().availableProcessors();
            // 查询待处理任务
            mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, corePoolSize);
            size = mediaProcessList.size();
            log.debug("取出待处理视频任务{}条", size);
            if(size <= 0){
                return ;
            }
        }catch (Exception e){
            log.debug("取出待处理视频任务异常："+ e.getMessage());
            return;
        }

        //启动 size 个线程的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(size);

        mediaProcessList.forEach(
                mediaProcess -> CompletableFuture.runAsync(() -> {
                    try {
                        processMediaProcess(mediaProcess);
                    } catch (Exception e) {
                        log.error("处理媒体任务失败: {}", mediaProcess, e);
                    }
                }, threadPool));

        //清除线程资源
//        threadPool.shutdown();
    }


    private void processMediaProcess(MediaProcess mediaProcess) throws Exception {
        try {
            //判断任务是否抢占成功
            boolean b = mediaFileProcessService.startTask(mediaProcess.getId());
            if (!b) {
                return;
            }
            log.debug("开始执行任务:{}", mediaProcess);

            //开始视频处理
            //下载处理文件到服务器上
//            mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "4",
//                    mediaProcess.getFileId(), null, "正在处理任务");

            File mp4File = null;
            try {
                mp4File = File.createTempFile("mp4-" + UUID.randomUUID(), ".mp4");
                mp4File.deleteOnExit();
            } catch (IOException e) {
                log.error("创建 mp4 临时文件失败");
                mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3",
                        mediaProcess.getFileId(), null, "创建 mp4 临时文件失败");
                return;
            }

            File originalFile = mediaFileService.downloadFileFromOSS(mp4File, mediaProcess.getBucket(),
                    mediaProcess.getFilePath());

            if (originalFile == null) {
                log.debug("下载待处理文件失败,originalFile:{}",
                        mediaProcess.getBucket().concat(mediaProcess.getFilePath()));

                mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3",
                        mediaProcess.getFileId(), null, "下载待处理文件失败");
                return;
            }

            //处理下载的视频文件
            String result = "";
            //根据备注来获取处理视频策略 5,2 (集数,清晰度)
//            String remark = mediaProcess.getRemark();
//            String episode = remark.split(",")[0];
//            String dpi = remark.split(",")[1];
            String targetFloderPath = null;
            String targetFilePath = null;
            Path tempDir = null;
            try {
                VideoQualityStrategy videoQualityStrategy = VideoQuality.getStrategyClassByCode(mediaProcess.getRemark()).
                        getDeclaredConstructor().newInstance();

                //创建临时文件夹
                tempDir = Files.createTempDirectory("oss-upload-files"+ UUID.randomUUID());

                targetFilePath = tempDir.toAbsolutePath().toString() +"/"+mediaProcess.getFilePath().replace("\\", "/");

                String tempFilePath = mediaProcess.getFilePath().substring(0, mediaProcess.getFilePath().lastIndexOf("/"));
                targetFloderPath = tempDir.toAbsolutePath().toString() +"/"+tempFilePath.replace("\\", "/");

                result = videoQualityStrategy.processVideoQuality(ffmpegPath, originalFile.getAbsolutePath(),
                        targetFilePath);

            } catch (Exception e) {
                log.error("处理视频文件:{},出错:{}", mediaProcess.getFilePath(), e.getMessage());
                return;
            }

            if (StringUtils.isEmpty(result)) {
                //记录错误信息
                log.error("处理视频失败,视频地址:{},错误信息:{}", mediaProcess.getBucket() + mediaProcess.getFilePath(), result);
                mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3",
                        mediaProcess.getFileId(), null, result);

                return;
            }
            //处理成功 上传到OSS

            String s = uploadVideoFileToOSS(targetFloderPath, mediaProcess.getBucket(), result,
                    mediaProcess.getFileId(), mediaProcess.getRemark() );

            if (StringUtils.isEmpty(s)) {
                mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3",
                        mediaProcess.getFileId(), null, "处理后视频上传或入库失败");

                return;
            }
            ///将 url 存储至数据库，并更新状态为成功，并将待处理视频记录删除存入历史
            mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "2",
                    mediaProcess.getFileId(), s, null);

        }catch (Exception e){
            log.error("处理媒体任务失败: {}", mediaProcess, e);
        }
    }


    /**
     * 分片广播 任务调度 视频处理 xiaojuzi
     * 并发处理视频 同步 取多个处理 等待
     */
//    @XxlJob("videoJobHandler")
    public void videoJobHandlerSync() throws Exception {

        // 分片序号
        int shardIndex = XxlJobHelper.getShardIndex();
        // 分片总数
        int shardTotal = XxlJobHelper.getShardTotal();

        log.debug("========== shardIndex:{}, shardTotal:{} =========", shardIndex, shardTotal);

        List<MediaProcess> mediaProcessList = null;
        int size = 0;

        try {
            // 查询cpu核心数
            int corePoolSize = Runtime.getRuntime().availableProcessors();
            // 查询待处理任务
            mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, corePoolSize);
            size = mediaProcessList.size();
            log.debug("取出待处理视频任务{}条", size);
            if(size < 0){
                return ;
            }
        }catch (Exception e){
            e.printStackTrace();
            log.debug("取出待处理视频任务异常："+ e.getMessage());
            return;
        }

        //启动 size 个线程的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(size);

        //计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        //将处理任务加入线程池
        mediaProcessList.forEach(mediaProcess -> {
            threadPool.execute(() -> {
                try {
                    //判断任务是否抢占成功
                    boolean b = mediaFileProcessService.startTask(mediaProcess.getId());
                    if (!b) {
                        return;
                    }
                    log.debug("开始执行任务:{}", mediaProcess);

                    //开始视频处理
                    //下载处理文件到服务器上
//                    mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "4",
//                            mediaProcess.getFileId(), null, "正在处理任务");

                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("mp4-" + UUID.randomUUID(), ".mp4");
                        mp4File.deleteOnExit();
                    } catch (IOException e) {
                        log.error("创建 mp4 临时文件失败");
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3",
                                mediaProcess.getFileId(), null, "创建 mp4 临时文件失败");
                        return;
                    }

                    File originalFile = mediaFileService.downloadFileFromOSS(mp4File, mediaProcess.getBucket(),
                            mediaProcess.getFilePath());

                    if (originalFile == null) {
                        log.debug("下载待处理文件失败,originalFile:{}",
                                mediaProcess.getBucket().concat(mediaProcess.getFilePath()));

                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3",
                                mediaProcess.getFileId(), null, "下载待处理文件失败");
                        return;
                    }

                    //处理下载的视频文件
                    String result = "";
                    //根据备注来获取处理视频策略 5,2 (集数,清晰度)
//            String remark = mediaProcess.getRemark();
//            String episode = remark.split(",")[0];
//            String dpi = remark.split(",")[1];
                    String targetFloderPath = null;
                    String targetFilePath = null;
                    Path tempDir = null;
                    try {
                        VideoQualityStrategy videoQualityStrategy = VideoQuality.getStrategyClassByCode(mediaProcess.getRemark()).
                                getDeclaredConstructor().newInstance();

                        //创建临时文件夹
                        tempDir = Files.createTempDirectory("oss-upload-files"+ UUID.randomUUID());

                        targetFilePath = tempDir.toAbsolutePath().toString() +"/"+mediaProcess.getFilePath().replace("\\", "/");

                        String tempFilePath = mediaProcess.getFilePath().substring(0, mediaProcess.getFilePath().lastIndexOf("/"));
                        targetFloderPath = tempDir.toAbsolutePath().toString() +"/"+tempFilePath.replace("\\", "/");

                        result = videoQualityStrategy.processVideoQuality(ffmpegPath, originalFile.getAbsolutePath(),
                                targetFilePath);

                    } catch (Exception e) {
                        log.error("处理视频文件:{},出错:{}", mediaProcess.getFilePath(), e.getMessage());
                        return;
                    }

                    if (StringUtils.isEmpty(result)) {
                        //记录错误信息
                        log.error("处理视频失败,视频地址:{},错误信息:{}", mediaProcess.getBucket() + mediaProcess.getFilePath(), result);
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3",
                                mediaProcess.getFileId(), null, result);

                        return;
                    }
                    //处理成功 上传到OSS

                    String s = uploadVideoFileToOSS(targetFloderPath, mediaProcess.getBucket(), result,
                            mediaProcess.getFileId(), mediaProcess.getRemark() );

                    if (StringUtils.isEmpty(s)) {
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3",
                                mediaProcess.getFileId(), null, "处理后视频上传或入库失败");

                        return;
                    }
                    ///将 url 存储至数据库，并更新状态为成功，并将待处理视频记录删除存入历史
                    mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "2",
                            mediaProcess.getFileId(), s, null);
            }finally {
                    //释放
                    countDownLatch.countDown();
                }
        });
        });
        //防止无限等待
        countDownLatch.await(60, TimeUnit.MINUTES);
    }


    private String uploadVideoFileToOSS(String fileFloder,String bucket,String result,String fileMd5,String dpi) {

        String dpi_path = new CustomVideoUtil().getVideoDpiPath(Integer.parseInt(dpi));

        //上传m3u8文件
        String m3u8FilePath = (fileFloder + "/encrypted_slice.m3u8").replace("\\", "/");
        String baseKeyPath = new File(fileFloder).getParentFile().getParentFile().getAbsolutePath()
                + "/" + fileMd5 + "/" + dpi_path;
        String keyInfo = (baseKeyPath + "/keyinfo.txt").replace("\\", "/");
        String keyFile = (baseKeyPath + "/encrypt.key").replace("\\", "/");

        //拼接m3u8FilePath所需要存储目录
        String[] pathParts  = fileFloder.replace("\\", "/").split("/");
        StringBuffer sb1 = new StringBuffer();
        for (int i = pathParts.length-3; i < pathParts.length; i++) {
            sb1.append(pathParts[i]);
            if (i < pathParts.length - 1) {
                sb1.append("/");
            }
        }
        sb1.append("/"+dpi_path);

        //拼接keyPath所需要存储目录
        String[] keyPathParts  = baseKeyPath.replace("\\", "/").split("/");

        StringBuffer sb2 = new StringBuffer();
        for (int i = keyPathParts.length-3; i < keyPathParts.length; i++) {
            sb2.append(keyPathParts[i]);
            if (i < keyPathParts.length - 1) {
                sb2.append("/");
            }
        }

        String obj1 = (sb1.toString()+"/encrypted_slice.m3u8").replace("\\", "/");
        String obj2 = (sb2.toString()+ "/keyinfo.txt").replace("\\", "/");
        String obj3 = (sb2.toString()+ "/encrypt.key").replace("\\", "/");

        //获取iv数据
        String iv = null;
        try (BufferedReader br = new BufferedReader(new FileReader(keyInfo))) {
            String line;
            int lineCount = 0;

            while ((line = br.readLine()) != null && lineCount < 3) {
                lineCount++;
                if (lineCount == 3) {
                    iv = line;
                }
            }

        } catch (IOException e) {
//            e.printStackTrace();
            log.error("读取keyInfo文件时候 出错:{}", e.getMessage());
            return null;
        }


        String url1 = null;
        String url2 = null;
        String url3 = null;
        String[] ts = null;
        OSS ossClient =null;
        try {
//            url1 = mediaFileService.addMediaFilesToOSS(m3u8FilePath,obj1, bucket);
            url1 = ossUtils.simpleUploadFile(obj1,m3u8FilePath,bucket);
            if (url1 == null) {
                return null;
            }
//            url2 = mediaFileService.addMediaFilesToOSS(keyInfo,obj2, bucket);
            url2 = ossUtils.simpleUploadFile(obj2,keyInfo,bucket);
            if (url2 == null) {
                return null;
            }
//            url3 = mediaFileService.addMediaFilesToOSS(keyFile,obj3, bucket);
            url3 = ossUtils.simpleUploadFile(obj3,keyFile,bucket);
            if (url3 == null) {
                return null;
            }

            String cleanedString = result.substring(1, result.length() - 1);
            ts = cleanedString.split(",");
            ossClient = ossUtils.createOSSClient();
            for (String filePath : ts) {
                //上传ts文件
                String tsPath = (fileFloder + "/" + filePath.trim()).replace("\\", "/");
                String obj4 = (sb1.toString()+ "/" + filePath.trim()).replace("\\", "/");

                PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, obj4, new FileInputStream(tsPath));
                putObjectRequest.setProcess("true");
                PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
                if (putObjectResult.getResponse().getStatusCode() == 200) {
                    log.info("Uploaded file to OSS successfully.{}",putObjectResult.getResponse().getUri());
                } else {
                    log.error("Error uploading file to OSS: "+ putObjectResult.getResponse().getStatusCode());
                    return null;
                }
            }

        } catch (Exception e) {
            log.error("上传视频失败或入库失败,视频id:{},错误信息:{}", fileMd5, e.getMessage());
            return null;
        }finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        //处理返回文件url路径
        int index1 = url1.replace("\\", "/").lastIndexOf("/");
        url1 =  url1.substring(0, index1);

        int index2 = url2.replace("\\", "/").lastIndexOf("/");
        url2 =  url2.substring(0, index2);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("video_base_url", url1);
        jsonObject.put("video_key_url", url2);
        jsonObject.put("video_ts_list", ts.length);
//        jsonObject.put("episode", episode);
        jsonObject.put("dpi", dpi);
        jsonObject.put("iv", iv);


        return jsonObject.toJSONString();
    }

}
