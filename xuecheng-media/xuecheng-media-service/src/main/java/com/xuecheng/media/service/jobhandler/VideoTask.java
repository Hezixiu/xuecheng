package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Linzkr
 * @description: TODO  XXL处理AVI视频
 * @date 2023/1/18 13:26
 */
@Component
public class VideoTask {
    @Autowired
    MediaFileProcessService mediaFileProcessService;
    @Autowired
    MediaFileService mediaFileService;
    @Value("${videoprocess.ffmpegpath}")
    String ffmpegPath;
    private static Logger logger = LoggerFactory.getLogger(VideoTask.class);



    /**
     * 视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardTotal, shardIndex, 8);
//        多线程处理
        if (mediaProcessList==null||mediaProcessList.isEmpty()){
            logger.debug("查询到的待处理视频为0");
            return ;
        }
        // 业务逻辑
//        要处理的任务数
        int size = mediaProcessList.size();
//        创建size个线程数量的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(size);
//        计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            threadPool.execute(()->{

                String processStatus = mediaProcess.getStatus();
                if ("2".equals(processStatus)){
                    logger.debug("视频已经处理不用再次处理");
                    countDownLatch.countDown(); //计数器减一
                    return;
                }
                String  fileId = mediaProcess.getFileId();
                String bucket = mediaProcess.getBucket();
                String filePath = mediaProcess.getFilePath();
//            源文件
                File originalFile = null;
//            mp4文件
                File targetFile = null;
                try {
                    originalFile = File.createTempFile("original", ".avi");
                    targetFile = File.createTempFile("mp4", ".mp4");
                } catch (IOException e) {
                    logger.error("处理视频前创建临时文件失败");
                    countDownLatch.countDown(); //计数器减一
                    return;
                }


                try {
                     mediaFileService.downloadFileFromMinIO(originalFile, bucket, filePath);
                } catch (Exception e) {
                    logger.error("下载源文件过程出错：{}",e.getMessage());
                    countDownLatch.countDown(); //计数器减一
                    return;

                }

                //转换后mp4文件的名称
                String mp4_name = fileId + ".mp4";
                //转换后mp4文件的路径
                String mp4_path = targetFile.getAbsolutePath();
                //创建工具类对象
                Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegPath, originalFile.getAbsolutePath(), mp4_name, mp4_path);
                //开始视频转换，成功将返回success
                String result = null;
                try {
                    result = videoUtil.generateMp4();
                } catch (Exception e) {
                    e.printStackTrace();
                    countDownLatch.countDown(); //计数器减一
                    return;
                }
                String resultStatus = "3";
                String url = null;
//                转换成功
                String objectName = getFilePathByMd5(fileId, ".mp4");
//                上传minIO
                mediaFileService.addMediaFilesToMinIO(mp4_path, bucket, objectName);
                resultStatus = "2";
                url = "/" + bucket + "/" + objectName;

                try {
                    mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), resultStatus, fileId,url,result );
                } catch (Exception e) {
                    logger.debug("保存任务结果出错");
                    countDownLatch.countDown(); //计数器减一
                    return;
                }
                countDownLatch.countDown();
            });
        });
//        阻塞到任务完成
//        等待  给一个超时时间 防止无限等待
        countDownLatch.await(30,TimeUnit.MINUTES);

    }

    private String getFilePathByMd5(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }
}
