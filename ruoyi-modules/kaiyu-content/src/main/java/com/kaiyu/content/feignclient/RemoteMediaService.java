package com.kaiyu.content.feignclient;

import com.kaiyu.content.domain.MediaFiles;
import com.kaiyu.content.domain.MediaProcess;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description: 媒资服务
 * @author: xiaojuzi
 * @create: 2024-04-01 10:49
 **/
@FeignClient(contextId = "RemoteMediaService", value = ServiceNameConstants.MEDIA_SERVICE, fallbackFactory = RemoteMediaFallbackFactory.class)
public interface RemoteMediaService {


    @ApiOperation("查询媒资详情接口")
    @PostMapping("/media/mediaFiles/getMediaFilesDetail")
    public R<MediaFiles> getMediaFilesDetail(@RequestParam("mediaId") String mediaId);

    @ApiOperation("查询媒资视频处理状态接口")
    @PostMapping("/media/mediaFiles/getMediaFilesStatus")
    public R<List<MediaProcess>> getMediaFilesStatus(@RequestParam("mediaId") String mediaId);


    
    
}
