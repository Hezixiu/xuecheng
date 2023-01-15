package com.xuecheng.content.api;

import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Linzkr
 * @description: TODO 关于课程计划的controller
 * @date 2023/1/15 15:49
 */
@RestController
@Slf4j
@Api("关于课程计划的controller")
public class TeachPlanController {
    @Autowired
    TeachPlanService teachPlanService;

    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTreeNodes(@PathVariable  Long courseId){
        return teachPlanService.findTeachPlayTree(courseId);
    }

    @PostMapping("teachplan")
    public void saveTeachPlan(@RequestBody  SaveTeachPlanDto saveTeachPlanDto){
        teachPlanService.saveTeachPlan(saveTeachPlanDto);
    }

}
