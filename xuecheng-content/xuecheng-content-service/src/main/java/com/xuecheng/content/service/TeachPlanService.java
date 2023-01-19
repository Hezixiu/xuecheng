package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachPlanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @author Linzkr
 * @description: TODO 课程计划查询对应的Service
 * @date 2023/1/15 16:31
 */
public interface TeachPlanService {
    List<TeachPlanDto> findTeachPlanTree(Long courseId);
    void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto);
    TeachplanMedia associationMedia(BindTeachPlanMediaDto bindTeachplanMediaDto);
    void delAssociationMedia(Long teachPlanId, String mediaId);
}
