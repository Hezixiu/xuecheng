package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.xuecheng.base.exception.XueChengException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author Linzkr
 * @description: TODO 课程管理service  CourseBaseInfoService接口实现类
 * @date  2023/1/14 17:37
 */
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Autowired
    private CourseMarketServiceImpl courseMarketService;
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
//      设置分页参数
        Page<CourseBase> pageInfo = new Page<>(pageParams.getPageCurrent(),pageParams.getPageSize());
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
//      模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
//      根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
//      根据发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());

//      查询到的结果
        Page<CourseBase> queryResult = courseBaseMapper.selectPage(pageInfo, queryWrapper);
//      准备返回的数据
//          查询到的数据
        List<CourseBase> records = queryResult.getRecords();
//          查询到的记录数
        long total = queryResult.getTotal();
        PageResult<CourseBase> pageResult = new PageResult<>(records,total, pageParams.getPageCurrent(), pageParams.getPageSize());

        return pageResult;
    }

    /**
     *
     * @param companyId   用户所属的公司ID
     * @param addCourseDto 传来的表单数据
     * @return 课程基本信息dto  主要整合了课程基本信息表和营销表中的数据
     */
    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
//        表单校验
        //合法性校验
        if (StringUtils.isBlank(addCourseDto.getName())) {
            XueChengException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(addCourseDto.getMt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(addCourseDto.getSt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(addCourseDto.getGrade())) {
            throw new RuntimeException("课程等级为空");
        }

        if (StringUtils.isBlank(addCourseDto.getTeachmode())) {
            throw new RuntimeException("教育模式为空");
        }

        if (StringUtils.isBlank(addCourseDto.getUsers())) {
            throw new RuntimeException("适应人群为空");
        }

        if (StringUtils.isBlank(addCourseDto.getCharge())) {
            throw new RuntimeException("收费规则为空");
        }



        CourseBase courseBase = new CourseBase();
//        设置公司ID
        courseBase.setCompanyId(companyId);
//        设置创建的时间
        courseBase.setCreateDate(LocalDateTime.now());
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");
        BeanUtils.copyProperties(addCourseDto, courseBase);
        int insertCourseBase = courseBaseMapper.insert(courseBase);
        CourseMarket courseMarket = new CourseMarket();
        courseMarket.setId(courseBase.getId());
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        int insertCourseMarket = this.saveCourseMarket(courseMarket);
        if (insertCourseBase<=0||insertCourseMarket<=0){
            throw new RuntimeException("添加课程失败");
        }
//        返回一个DTO对象 整合了 基本信息和营销信息
        CourseBaseInfoDto courseBaseInfoDto = getCourseBaseInfoDto(courseBase.getId());
        return courseBaseInfoDto;
    }

    /**
     *
     * @param courseId  根据课程ID
     * @return          返回课程基本信息DTO
     */
    public CourseBaseInfoDto getCourseBaseInfoDto(Long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
//        根据课程分类的ID查询分类的名称
        String mt = courseBase.getMt();
        String st = courseBase.getSt();

        CourseCategory mtCourseCategory = courseCategoryMapper.selectById(mt);
        CourseCategory stCourseCategory = courseCategoryMapper.selectById(st);
        if (mtCourseCategory!=null){
            String mtName = mtCourseCategory.getName();
            courseBaseInfoDto.setMtName(mtName);
        }
        if (stCourseCategory!=null){
            String stName = stCourseCategory.getName();
            courseBaseInfoDto.setStName(stName);
        }
        return courseBaseInfoDto;
    }
    /**
     *
     * @param companyId  本机构只能修改本机构的课程
     * @param editCourseDto  修改表单的数据
     * @return  课程基本信息
     */
    @Override
    @Transactional
    public CourseBaseInfoDto updateCourseBase(Long companyId,EditCourseDto editCourseDto) {
        Long id = editCourseDto.getId();
//        创建一个CourseBase
        CourseBase courseBase = courseBaseMapper.selectById(id);
        if (courseBase==null){
            XueChengException.cast("课程不存在");
        }
//        判断表单中的公司ID是否是本机构的公司ID
        if (!courseBase.getCompanyId().equals(companyId)) {
            throw new XueChengException("禁止修改非本机构的课程");
        }
        BeanUtils.copyProperties(editCourseDto,courseBase );
        courseBase.setChangeDate(LocalDateTime.now());


//          创建一个courseMarket
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto, courseMarket);
//        将两个对象添加到数据库
        int update = courseBaseMapper.updateById(courseBase);
        int i = this.saveCourseMarket(courseMarket);
        CourseBaseInfoDto courseBaseInfoDto = this.getCourseBaseInfoDto(id);
        return courseBaseInfoDto;
    }

//    抽取对营销数据的保存
    private int saveCourseMarket(CourseMarket courseMarket){
        String chargeType = courseMarket.getCharge();
        if (StringUtils.isBlank(chargeType)){
            XueChengException.cast("选择收费规则");
        }
//        如果消费类型为收费  那么课程的收费价格不能为空且必须大于0
        if (chargeType.equals("201001")){
            if (courseMarket.getPrice()==null||courseMarket.getPrice()<=0){
                XueChengException.cast("课程的收费价格不能为空且必须大于0");
            }
            if (courseMarket.getOriginalPrice()==null||courseMarket.getOriginalPrice()<=0){
                XueChengException.cast("课程的收费价格不能为空且必须大于0");
            }
        }
        boolean saveOrUpdate = courseMarketService.saveOrUpdate(courseMarket);
        return saveOrUpdate?1:0;

    }
}
