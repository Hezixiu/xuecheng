package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author Linzkr
 * @description: TODO 课程分类属性节点 将来Controller会返回这个类型的数据
 * @date 2023/1/14 20:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CourseCategoryTreeDto extends CourseCategory {
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
