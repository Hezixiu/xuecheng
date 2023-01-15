package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * 课程分类操作相关的service
 */
public interface CourseCategoryService {

    List<CourseCategoryTreeDto> queryTreeNodes(String id);

}
