package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.po.CourseBase;
import org.springframework.stereotype.Repository;

//课程基本信息的Mapper接口
@Repository
//Repository注解 和mapper注解都可以加载mapper接口上 但是Repository注解需要单独配置MapperScan注解声明mapper在哪个包 但是mapper注解可以单独使用
public interface CourseBaseMapper extends BaseMapper<CourseBase> {

}
