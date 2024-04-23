package com.kaiyu.media.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaiyu.media.domain.*;
import com.kaiyu.media.domain.dto.QueryMediaParamsDto;
import com.kaiyu.media.domain.dto.UploadFileParamsDto;
import com.kaiyu.media.domain.dto.UploadFileResultDto;
import com.kaiyu.media.mapper.MediaFilesMapper;
import com.kaiyu.media.mapper.MediaProcessMapper;
import com.kaiyu.media.service.MediaFileService;
import com.kaiyu.media.util.FileTypeUtil;
import com.kaiyu.media.util.OssUtils;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import com.ruoyi.common.core.utils.uuid.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-01 15:17
 **/
@Service
public class MediaFileServiceImpl implements MediaFileService {

    private static final Logger log = LoggerFactory.getLogger(MediaFileServiceImpl.class);

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    @Lazy
    MediaFileService currentProxy;

    @Autowired
    MediaProcessMapper mediaProcessMapper;

    @Autowired
    OssUtils ossUtils;

    @Value("${aliyun.oss.files.bucketName}")
    private String bucket_files;

    @Value("${aliyun.oss.videofiles.bucketName}")
    private String video_files;

    @Override
    public PageResult<MediaFiles> queryMediaFiles(PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(queryMediaParamsDto.getFilename()), MediaFiles::getFilename, queryMediaParamsDto.getFilename());
        queryWrapper.eq(!StringUtils.isEmpty(queryMediaParamsDto.getFileType()), MediaFiles::getFileType, queryMediaParamsDto.getFileType());

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());

        return mediaListResult;
    }

    @Override
    public List<MediaFiles> queryMediaVideoFiles(QueryMediaParamsDto queryMediaParamsDto) {
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(queryMediaParamsDto.getFilename()), MediaFiles::getFilename, queryMediaParamsDto.getFilename());
        queryWrapper.eq(MediaFiles::getFileType, "001002");
        List<MediaFiles> mediaFiles = mediaFilesMapper.selectList(queryWrapper);
        return mediaFiles;
    }

    @Override
    public UploadFileResultDto uploadFile(UploadFileParamsDto uploadFileParamsDto,String localFilePath,String folder, String objectName) {

        File file = new File(localFilePath);
        if (!file.exists()) {
            log.debug("文件不存在");
            KaiYuEducationException.cast("文件不存在");
        }
        if (StringUtils.isEmpty(folder)) {
            // 如果目录不存在，则自动生成一个目录
            folder = getDefaultFolderPath();
        }else if (!folder.endsWith("/")) {
            // 如果目录末尾没有 / ，加一个
            folder = folder + "/";
        }

        String fileMd5 = getFileMd5(file);

        if (fileMd5 == null){
            log.debug("文件md5为空");
            KaiYuEducationException.cast("文件md5为空");
        }

        //进行文件md5值校验 没有才让继续上传
        MediaFiles mediaFiles1 = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles1 != null) {
            return null;
        }

        String filename = uploadFileParamsDto.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));

        if (StringUtils.isEmpty(objectName)) {
            // 如果文件名为空，则设置其默认文件名为文件的md5码 + 文件后缀名
            objectName = fileMd5 + extension;
        }

        objectName = folder + objectName;

        try {
            //将文件上传到oss
            String url = addMediaFilesToOSS(localFilePath, objectName, bucket_files);
            if (url == null) {
                log.debug("将文件上传到oss上传过程中出错");
                KaiYuEducationException.cast("上传文件到文件系统失败");
            }
            //将文件上传到DB
            MediaFiles mediaFiles = currentProxy.addMediaFilesToDB(uploadFileParamsDto, objectName, fileMd5,bucket_files,url);
            UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
            BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
            return uploadFileResultDto;
        } catch (Exception e) {
            log.debug("上传过程中出错：{}", e.getMessage());
            KaiYuEducationException.cast("上传过程中出错" + e.getMessage());
        }
        return null;
    }

    /**
     * 将上传文件记录添加到数据库
     * xiaojuzi
     * @param uploadFileParamsDto
     * @param objectName
     * @param fileMD5
     * @param bucket
     * @return
     */
    @Override
    @Transactional
    public MediaFiles addMediaFilesToDB(UploadFileParamsDto uploadFileParamsDto, String objectName, String fileMD5,
                                        String bucket,String url) {
        // 根据文件名获取Content-Type
        String contentType = uploadFileParamsDto.getContentType();
        // 保存到数据库
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMD5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMD5);
            mediaFiles.setFileId(fileMD5);
            mediaFiles.setBucket(bucket);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setStatus("1");
            mediaFiles.setFilePath(objectName);
            mediaFiles.setUrl(url);
            mediaFiles.setTags(FileTypeUtil.getTagByFileTypeCode(uploadFileParamsDto.getFileType()));
            //设置备注
            mediaFiles.setRemark(uploadFileParamsDto.getRemark());
//            if ("video/mp4".equals(contentType)) {
//                JSONObject data = new JSONObject();
//                String remark = uploadFileParamsDto.getRemark();
//                String episode = remark.split(",")[0];
//                String dpi = remark.split(",")[1];
//                data.put("episode", episode);
//                data.put("dpi", dpi);
//                mediaFiles.setRemark(JSON.toJSONString(data));
//            }

            //审核状态待设计 xiaojuzi
//            mediaFiles.setAuditStatus("002003");
        }
        int mediaFilesInsert = mediaFilesMapper.insert(mediaFiles);
        if (mediaFilesInsert <= 0) {
            KaiYuEducationException.cast("保存文件信息失败,该文件已经存在或数据库异常");
        }

        // 如果是mp4视频，则额外添加至视频待处理表 视频需要加密切片等操作 xiaojuzi
        if ("video/mp4".equals(contentType)) {
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles, mediaProcess);
            // 未处理状态为1 已处理为2 处理失败为3 处理中为4 xiaojuzi
            mediaProcess.setStatus("1");
            //失败次数默认为0
            mediaProcess.setFailCount(0);
            //处理remark标注
            mediaProcess.setRemark(uploadFileParamsDto.getRemark());

            int processInsert = mediaProcessMapper.insert(mediaProcess);

            if (processInsert <= 0) {
                KaiYuEducationException.cast("保存mp4视频到待处理表失败");
            }
        }
        return mediaFiles;
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        //查询文件信息
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //文件存在
            String bucket = mediaFiles.getBucket();
            String filePath = mediaFiles.getFilePath();
            OSSObject ossObject = null;
            try {
                // 从OSS获取对象流
                GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, filePath);
                ossObject = ossUtils.createOSSClient().getObject(getObjectRequest);

                if (ossObject != null) {
                    //文件已存在
                    return RestResponse.success(true);
                }
            } catch (Exception e) {
                log.debug("文件上传前检查文件,获取文件失败：{}", e.getMessage());
            }finally {
                if (ossObject != null) {
                    try {
                        ossObject.close();
                    } catch (IOException e) {
                        log.debug("文件上传前检查文件,Error Downloading file from OSS: " + e.getMessage());
                    }
                }
            }
        }
        //文件不存在
        return RestResponse.success(false);
    }


    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {

        //得到分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);

        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;

        OSSObject ossObject = null;

        try {
            // 从OSS获取对象流
            GetObjectRequest getObjectRequest = new GetObjectRequest(video_files, chunkFilePath);
            ossObject = ossUtils.createOSSClient().getObject(getObjectRequest);

            if (ossObject != null) {
                //文件已存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            log.debug("文件上传前检查分块,获取文件失败：{}", e.getMessage());
        } finally {
            if (ossObject != null) {
                try {
                    ossObject.close();
                } catch (IOException e) {
                    log.debug("文件上传前检查文件,Error Downloading file from OSS: " + e.getMessage());
                }
            }

        }
        //分块未存在
        return RestResponse.success(false);
    }


    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk,String localChunkFilePath) {
        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;
        //将文件存储至 OSS
        String url = addMediaFilesToOSS(localChunkFilePath,chunkFilePath,video_files);
        if (url == null) {
            log.debug("上传分块文件失败:{}", chunkFilePath);
            return RestResponse.validfail( "上传分块失败",false);
        }
        log.debug("上传分块文件成功:{}",chunkFilePath);
        return RestResponse.success(url,true);
    }


    @Override
    public RestResponse mergeChunks(String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {

        //获取分块文件路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);

        //获取合并文件列表
        OSS ossClient = ossUtils.createOSSClient();
        List<OSSObjectSummary> objects = ossClient.listObjects(video_files, chunkFileFolderPath).getObjectSummaries();
        //文件排序
        List<OSSObjectSummary> sortedObjects = objects.stream()
                .sorted((s1, s2) -> {
                            Long num1 = extractNumber(s1.getKey());
                            Long num2 = extractNumber(s2.getKey());
                            return Long.compare(num1, num2);})
                .collect(Collectors.toList());

        if(objects.size() != chunkTotal) {
            log.debug("分块文件合并失败,分块文件缺失");
            return RestResponse.validfail("分块文件合并失败,分块文件缺失",false);
        }

        //文件名称
        String fileName = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));

        if (extName.equals(".mp4")){
            //暂时只让mp4大文件上传
            uploadFileParamsDto.setContentType("video/mp4");
        }

        //合并文件路径
        String mergeFilePath = getFilePathByMd5(fileMd5, extName);

        //异步处理
        CompletableFuture.supplyAsync(() -> {
            File ossFile = null;
            try {
                //方案一 下载到本地合并后在上传
                // 创建临时文件夹
//            Path tempDir = Files.createTempDirectory("oss-merge-files"+ UUID.randomUUID());
                // 下载分块文件到临时文件夹并合并
//            ByteArrayOutputStream mergedContent = new ByteArrayOutputStream();

                // 创建临时文件
                ossFile = File.createTempFile("oss-"+ UUID.randomUUID(), ".merge");
                //JVM退出时删除临时文件
                ossFile.deleteOnExit();

                try (FileOutputStream fos = new FileOutputStream(ossFile)) {
                    for (OSSObjectSummary summary : sortedObjects) {
                        OSSObject object = ossClient.getObject(video_files, summary.getKey());
                        try (InputStream fileStream = object.getObjectContent()) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = fileStream.read(buffer)) != -1) {
                                fos.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                }

                //oss上文件的 md5 值
                String md5Hex = getFileMd5(ossFile);

                if(!fileMd5.equals(md5Hex)){
                    return RestResponse.validfail( "文件合并校验失败，最终上传失败",false);
                }

                ossClient.putObject(video_files, mergeFilePath,ossFile);
//            CompletableFuture<PutObjectResult> future = CompletableFuture.supplyAsync(() -> {
//                // 将合并后的内容上传到 OSS
////                ossClient.putObject(video_files, mergeFilePath,ossFile);
////                PutObjectRequest putObjectRequest = new PutObjectRequest(video_files, mergeFilePath, ossFile);
////                putObjectRequest.setProcess("true");
////                PutObjectResult result = ossClient.putObject(putObjectRequest);
//                return result;
//            });

                // 删除临时文件夹及其内容
//            Files.delete(tempDir);

//            System.out.println("文件合并并上传成功！");

                log.debug("文件合并并上传成功,fileMd5:{},",fileMd5);

                //方式二 如果对象可追加的话 云端合并
//            //创建合并后的OSS对象文件
//            ObjectMetadata metadata = new ObjectMetadata();
//            ossClient.putObject(video_files, mergeFilePath, new ByteArrayInputStream(new byte[0]), metadata);
//
//            //流式追加
//            long position = 0L;
//            for (OSSObjectSummary summary : objects) {
//                OSSObject object = ossClient.getObject(video_files, summary.getKey());
//                try (InputStream fileStream = object.getObjectContent()) {
//                    AppendObjectRequest appendRequest = new AppendObjectRequest(video_files, mergeFilePath, fileStream);
//                    appendRequest.setPosition(position);
//
//                    AppendObjectResult appendObjectResult = ossClient.appendObject(appendRequest);
//                    position = appendObjectResult.getNextPosition();
//                }
//            }

            } catch (Exception e) {

                log.debug("合并文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage());

                return RestResponse.validfail( "合并文件失败",false);
            } finally {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
            //文件大小
            uploadFileParamsDto.setFileSize(ossFile.length());

            //文件入库
            currentProxy.addMediaFilesToDB(uploadFileParamsDto,mergeFilePath,fileMd5,video_files,"");

            //清除分块文件
            Boolean b = clearChunkFiles(chunkFileFolderPath);
            //进行预防 二次清除
            if (!b) {
                clearChunkFiles(chunkFileFolderPath);
            }
            return RestResponse.success(true);

        }).exceptionally(ex ->{
            log.error("异步操作失败: " + ex.getMessage());
            return RestResponse.validfail("异步操作失败: " + ex.getMessage());
        });


        return RestResponse.success(true);

    }


    /**
     * 获取文件默认存储目录路径 年/月/日
     *  xiaojuzi
     */
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/")+"/";
        return folder;
    }

    /**
     * 获取文件的 md5值
     * xiaojuzi
     */
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
//            e.printStackTrace();
            log.info("获取文件md5失败,异常:{}",e.getMessage());
            return null;
        }
    }

    /**
     * 自定义排序器需要的方法
     * @param s
     * @return
     */
    private Long extractNumber(String s) {
        String[] parts = s.split("/");
        String lastPart = parts[parts.length - 1];
        String numString = lastPart.trim();
//        String numString = lastPart.replaceAll("\\D+", "");
        return Long.parseLong(numString);
    }


    /**
     * @param localFilePath   文件地址
     * @param objectName 对象名称 23/02/15/porn.mp4
     */
    public String addMediaFilesToOSS(String localFilePath,String objectName,String bucket) {
        String url = ossUtils.simpleUploadFile(objectName, localFilePath,bucket);
        return url;
    }


    /**
     * 获取分块文件的目录，例如文件的md5码为  1f2465f， 那么该文件的分块放在 /1/f/1f2465f下，即前两级目录为md5的前两位
     * xiaojuzi
     * @param fileMd5
     * @return
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    /**
     * 得到合并后的文件的地址
     * @param fileMd5
     * @param fileExt
     * @return
     */
    private String getFilePathByMd5(String fileMd5,String fileExt){
        return fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

    /**
     * 从OSS下载文件
     * @param bucket
     * @param objectName
     * @return
     */
    @Override
    public File downloadFileFromOSS(File file, String bucket,String objectName){

        try{

            String absolutePath = file.getAbsolutePath();

            //覆盖临时文件
            Boolean b = ossUtils.simpleDownloadFile(objectName, absolutePath, bucket);

            if (!b){
                log.debug("Error Downloading file from OSS: " + b);
                return null;
            }

            return file;

        } catch (Exception e){
            e.printStackTrace();
            log.debug("Error Downloading file from OSS: " + e.getMessage());
            }
        return null;
    }

    @Override
    public MediaFiles getFileById(String mediaId) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(mediaId);
        if (mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())) {
//            KaiYuEducationException.cast("视频还没有转码处理");
            log.info("媒资文件还没有上传或处理成功");
            return null;
        }
        return mediaFiles;
    }

    /**
     * 清除分块文件
     * @param chunkFileFolderPath
     */
    private Boolean clearChunkFiles(String chunkFileFolderPath){
        try {
            //获取分块文件夹 进行分批删除
            OSS ossClient = ossUtils.createOSSClient();
            List<OSSObjectSummary> objects = ossClient.listObjects(video_files, chunkFileFolderPath).getObjectSummaries();
            for (OSSObjectSummary objectSummary : objects) {
                String objectName = objectSummary.getKey();
                ossClient.deleteObject(video_files, objectName);
                log.info("清除分块文件:{}",objectName);
            }
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("清除分块文件失败,chunkFileFolderPath:{},异常:{}",chunkFileFolderPath,e.getMessage());
            return false;
        }
    }

}
