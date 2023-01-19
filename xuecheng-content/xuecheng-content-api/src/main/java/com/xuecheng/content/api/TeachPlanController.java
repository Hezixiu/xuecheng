package com.xuecheng.content.api;

import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.BindTeachPlanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
        return teachPlanService.findTeachPlanTree(courseId);
    }

    @PostMapping("teachplan")
    public void saveTeachPlan(@RequestBody  SaveTeachPlanDto saveTeachPlanDto){
        teachPlanService.saveTeachPlan(saveTeachPlanDto);
    }
    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    void associationMedia(@RequestBody BindTeachPlanMediaDto bindTeachplanMediaDto){
        teachPlanService.associationMedia(bindTeachplanMediaDto);
    }

//    @ApiOperation(value = "课程计划和媒资信息解除绑定")
//    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
//    void delAssociationMedia(@PathVariable Long teachPlanId,@PathVariable Long mediaId){
//
//    }


}
