package com.xuecheng.media.api;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.xuecheng.base.exception.XueChengException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Linzkr
 * @description: TODO
 * @date 2023/1/19 19:50
 */
@Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
@RestController
@RequestMapping("/open")
public class MediaOpenController {
    @Autowired
    MediaFileService mediaFileService;

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId){

        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        if(mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())){
            XueChengException.cast("视频还没有转码处理");
        }
        return RestResponse.success(mediaFiles.getUrl());

    }


}
