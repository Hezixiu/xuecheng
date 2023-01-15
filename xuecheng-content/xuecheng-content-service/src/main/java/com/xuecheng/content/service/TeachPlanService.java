package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.xuecheng.content.model.dto.TeachPlanDto;

import java.util.List;

/**
 * @author Linzkr
 * @description: TODO 课程计划查询对应的Service
 * @date 2023/1/15 16:31
 */
public interface TeachPlanService {
    List<TeachPlanDto> findTeachPlayTree(Long courseId);
    void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto);
}
