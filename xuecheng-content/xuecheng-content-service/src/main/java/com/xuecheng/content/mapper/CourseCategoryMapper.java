package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 *
 */
@Repository
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
    /**
     *
     *
     * @return 返回一个DTO的list对象 里面有一个可以递归的DTO对象
     */
    List<CourseCategoryTreeDto> selectTreeNodes(String id);
}
