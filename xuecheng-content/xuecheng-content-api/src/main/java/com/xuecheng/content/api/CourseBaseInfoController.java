package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.impl.CourseBaseInfoServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

// api注解 修饰整个类，描述controller的作用

@Api(value = "课程管理相关的接口",tags = "课程管理相关的接口")
@Controller
@RestController
public class CourseBaseInfoController {
    @Autowired
    private CourseBaseInfoService courseBaseInfoService;
    @ApiOperation("描述一个类的一个方法，或者说一个接口的作用")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto){
//        调用service获取数据
        PageResult<CourseBase> pageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        return pageResult;
    }
    @ApiOperation("新增课程")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody  @Validated AddCourseDto addCourseDto){
//        获取当前登录用户的所属公司机构ID
        Long companyId = 22L;
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.createCourseBase(companyId, addCourseDto);
        return courseBaseInfoDto;
    }

//    以下是加了JSR303功能并且有分组校验的
//    @ApiOperation("新增课程")
//    @PostMapping("/course2")
//    public CourseBaseInfoDto createCourseBase2(@RequestBody @Validated(ValidationGroups.Insert.class)  AddCourseDto addCourseDto){
////        获取当前登录用户的所属公司机构ID
//        Long companyId = 22L;
//        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.createCourseBase(companyId, addCourseDto);
//        return courseBaseInfoDto;
//    }
    @ApiOperation("根据ID查询课程基本信息 信息包括base表和market表中的信息")
    @GetMapping("/course/{id}")
    public CourseBaseInfoDto getCourseBaseInfoById(@PathVariable("id") Long courseId ){

            return courseBaseInfoService.getCourseBaseInfoDto(courseId);
    }
    @ApiOperation("根据ID查询课程基本信息 信息包括base表和market表中的信息")
    @PutMapping("/course")
    public CourseBaseInfoDto updateCourseBase(@RequestBody EditCourseDto editCourseDto ){
        //机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId,editCourseDto);
    }


}
