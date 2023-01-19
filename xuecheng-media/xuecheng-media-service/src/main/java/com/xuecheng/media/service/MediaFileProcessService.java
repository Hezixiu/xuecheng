package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Linzkr
 * @description: TODO  分片作业  处理AVI视频
 * @date 2023/1/18 16:09
 */

public interface MediaFileProcessService {
    /**
     * @Author Linzkr
     * @Description
     * @Date 2023/1/18 16:10
     * @param shardTotal  分片总数
	 * @param shardIndex   分片序号
	 * @param count 分片记录数
     * @return MediaProcess对象集合 该集合会传入分片作业XxL Job处理
     */
    public List<MediaProcess> getMediaProcessList(int shardTotal,  int shardIndex, int count );

    /**
     * @Author Linzkr
     * @Description
     * @Date 2023/1/18 16:10
     * @param taskId   任务id
     * @param status   任务状态
     * @param fileId   文件id
     * @param url      url
     * @param errorMsg 错误信息
     * @return void
     */
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);

}
