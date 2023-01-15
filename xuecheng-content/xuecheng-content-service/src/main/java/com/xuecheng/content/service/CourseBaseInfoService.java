package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
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
}
