package com.xuecheng.content.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.XueChengException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Linzkr
 * @description: TODO 课程发布的Service的实现类
 * @date 2023/1/19 18:06
 */
@Service
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @Autowired
    TeachPlanService teachPlanService;
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    CoursePublishMapper coursePublishMapper;
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseBaseInfoDto(courseId);
//        获取教学计划树
        List<TeachPlanDto> teachPlayTree = teachPlanService.findTeachPlanTree(courseId);
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setTeachPlans(teachPlayTree);

        return coursePreviewDto;
    }

    @Override
    public void commitAudit(Long companyId, Long courseId) {
        //约束校验
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //课程审核状态
        String auditStatus = courseBase.getAuditStatus();
        //当前审核状态为已提交未审核 不允许再次提交
        if("202003".equals(auditStatus)){
            XueChengException.cast("当前为等待审核状态，审核完成可以再次提交。");
        }
        //本机构只允许提交本机构的课程
        if(!courseBase.getCompanyId().equals(companyId)){
            XueChengException.cast("不允许提交其它机构的课程。");
        }

        //课程图片是否填写
        if(StringUtils.isEmpty(courseBase.getPic())){
            XueChengException.cast("提交失败，请上传课程图片");
        }
//        查询课程计划信息
        List<TeachPlanDto> teachPlayTree = teachPlanService.findTeachPlanTree(courseId);
        if (teachPlayTree.size() == 0){
            XueChengException.cast("提交失败，还没有添加课程计划");
        }
        CoursePublishPre coursePublishPre = new CoursePublishPre();
//      封装数据 主要有coursebase coursemarket  teachplan
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseBaseInfoDto(courseId);
        BeanUtils.copyProperties(courseBaseInfoDto, coursePublishPre);
//        所有的课程计划转换为JSON  插入到表中
        String teachPlanJSON = JSON.toJSONString(teachPlayTree);
        coursePublishPre.setTeachplan(teachPlanJSON);
//        课程营销信息转换为JSON 插入到表中
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        String marketJSON = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(marketJSON);
//        设置状态为已提交未审核
        coursePublishPre.setStatus("202003");
//        设置创建记录的时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
//        从表中获取记录
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
//        如果为NULL 代表之前还没有提交过 可以插入
        if (coursePublishPreUpdate==null){
            coursePublishPreMapper.insert(coursePublishPre);
        }else{
            coursePublishPreMapper.updateById(coursePublishPre);
        }
//        插入成功后 更新base表 中的状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }


    @Override
    public void publish(Long companyId, Long courseId) {
        //约束校验
        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre == null){
            XueChengException.cast("请先提交课程审核，审核通过才可以发布");
        }
        //本机构只允许提交本机构的课程
        if(!coursePublishPre.getCompanyId().equals(companyId)){
            XueChengException.cast("不允许提交其它机构的课程。");
        }


        //课程审核状态
        String auditStatus = coursePublishPre.getStatus();
        //审核通过方可发布
        if(!"202004".equals(auditStatus)){
            XueChengException.cast("操作失败，课程审核通过方可发布。");
        }

        //保存课程发布信息
        saveCoursePublish(courseId);

        //保存消息表
        saveCoursePublishMessage(courseId);

        //删除课程预发布表对应记录
        coursePublishPreMapper.deleteById(courseId);

    }
    //保存消息表
    private void saveCoursePublishMessage(Long courseId) {


    }

    //保存课程发布信息
    private void saveCoursePublish(Long courseId) {
//        从预发布表中获取记录
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
//          如果预发布表中没有抛出异常
        if (coursePublishPre==null){
            XueChengException.cast("您还没有提交记录");
        }
//        如果审核没有通过 那么抛出异常
        if ("202004".equals(coursePublishPre.getStatus())){
            XueChengException.cast("审核未通过 不能发布课程");
        }
//        向publish表插入数据 如果没有数据 插入 如果有就更新
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setStatus("203002");
//        从 publish表中拿去记录
        CoursePublish coursePublishFromDB = coursePublishMapper.selectById(coursePublishPre.getId());
//        如果记录为空 说明需要插入
        if (coursePublishFromDB==null){
            coursePublishMapper.insert(coursePublish);
        }else {
//            如果记录不为空说明需要更新
            coursePublishMapper.updateById(coursePublish);
        }
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }
}
