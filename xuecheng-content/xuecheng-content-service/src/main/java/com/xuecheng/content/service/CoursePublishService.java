package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

/**
 * @author Linzkr
 * @description: TODO  课程发布的Service
 * @date 2023/1/19 18:04
 */
public interface CoursePublishService {
    /**
     * @Author Linzkr
     * @Description 获取到预览所需的所有数据
     * @Date 2023/1/19 18:05
     * @param courseId 根据课程ID 获取到预览所需的所有数据
     * @return   返回课程预览DTO
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);
    /**
     * @Author Linzkr
     * @Description
     * @Date 2023/1/19 21:15
     * @param companyId 公司ID 每个人只能提交本公司的课程
	 * @param courseId   课程ID
     * @return void
     */
    public void commitAudit(Long companyId,Long courseId);
    /**
     * @Author Linzkr
     * @Description
     * @Date 2023/1/19 22:51
     * @param companyId 公司ID
	 * @param courseId  课程ID
     * @return
     */
    public void  publish(Long companyId,Long courseId);
}
