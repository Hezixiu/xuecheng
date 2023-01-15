package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.springframework.web.bind.annotation.RequestBody;
//课程管理service
public interface CourseBaseInfoService {
//    课程查询

    /**
     *
      *@param pageParams 分页参数
     * @param queryCourseParamsDto  查询条件
     * @return 返回值是pageResult
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto);

    /**
     *
     * @param companyId  设置公司ID
     * @param addCourseDto  将addCourseDto分为两个类  加入到base表和market表
     * @return  将addCourseDto封装为基本信息DTO类并返回
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     *
     * @param courseId  根据课程ID查询表
     * @return  返回课程基本信息DTO类
     */
    CourseBaseInfoDto getCourseBaseInfoDto(Long courseId);

    /**
     *
     * @param companyId  本机构只能修改本机构的课程
     * @param editCourseDto  修改表单的数据
     * @return  课程基本信息
     */
    CourseBaseInfoDto updateCourseBase(Long companyId,EditCourseDto editCourseDto);
}
