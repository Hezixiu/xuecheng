package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


@Data
@ToString
@ApiModel(value="AddCourseDto", description="新增课程基本信息")
public class EditCourseDto extends AddCourseDto{
// 课程id
    private Long id;

}
