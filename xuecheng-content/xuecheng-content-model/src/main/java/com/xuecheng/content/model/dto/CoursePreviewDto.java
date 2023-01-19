package com.xuecheng.content.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Linzkr
 * @description: TODO  前台预览媒资的DTO
 * @date 2023/1/19 18:00
 */
@Data
public class CoursePreviewDto {

    //课程基本信息,课程营销信息
    CourseBaseInfoDto courseBase;


    //课程计划信息
    List<TeachPlanDto> teachPlans;

    //师资信息暂时不加...


}

