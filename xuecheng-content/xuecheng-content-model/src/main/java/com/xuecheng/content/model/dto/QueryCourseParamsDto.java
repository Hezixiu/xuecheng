package com.xuecheng.content.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

//查询课程 查询条件的DTO
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QueryCourseParamsDto {
//    审核状态
    private String auditStatus;
//    课程名称
    private String courseName;
//    发布状态
    private String publishStatus;

}
