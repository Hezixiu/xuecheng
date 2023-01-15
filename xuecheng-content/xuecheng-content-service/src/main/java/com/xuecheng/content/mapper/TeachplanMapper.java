package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.po.Teachplan;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Repository
public interface TeachplanMapper extends BaseMapper<Teachplan> {
//    查询课程计划 返回一种树形结构的结果（类似于查询课程类型）
    public List<TeachPlanDto> selectTreeNodes(Long courseId);
}
