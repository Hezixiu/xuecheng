package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachPlanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Linzkr
 * @description: TODO 课程计划查询对应的Service实现类
 * @date 2023/1/15 16:33
 */
@Service
public class TeachPlanServiceImpl implements TeachPlanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    /**
     *
     * @param courseId  根据课程ID
     * @return 返回课程的计划任务 （属性结构）
     */
    @Override
    public List<TeachPlanDto> findTeachPlanTree(Long courseId) {
        List<TeachPlanDto> teachPlanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachPlanDtos;
    }

    /**
     *
     * @param saveTeachPlanDto  前端传过来的参数
     */
    @Override
    public void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto) {
        Long id = saveTeachPlanDto.getId();
        Teachplan teachPlan = teachplanMapper.selectById(id);
//        如果teachPlan为null就代表是新增一个计划
        if (teachPlan==null){
            teachPlan=new Teachplan();
            Long parentId = saveTeachPlanDto.getParentid();
            int teachPlanCount = getTeachPlanCount(saveTeachPlanDto.getCourseId(),parentId);
            BeanUtils.copyProperties(saveTeachPlanDto, teachPlan);
            teachPlan.setOrderby(teachPlanCount+1);
            teachplanMapper.insert(teachPlan);
        }else {
//            如果teachPlan不为null就代表是修改计划内容
            BeanUtils.copyProperties(saveTeachPlanDto, teachPlan);
            teachplanMapper.updateById(teachPlan);
        }
    }
    //    找到同级别的课程计划的数量
    public int getTeachPlanCount(Long courseId,Long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;

    }
    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachPlanMediaDto bindTeachplanMediaDto) {
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);

        if (teachplan==null){
            XueChengException.cast("根据ID找不到该课程计划");
        }
        Integer grade = teachplan.getGrade();
        if (grade!=2){
            XueChengException.cast("只有二级课程能添加媒体");
        }
//        删除旧的绑定关系
        LambdaQueryWrapper<TeachplanMedia> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId);
        teachplanMediaMapper.delete(lambdaQueryWrapper);
//       添加新的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    @Override
    public void delAssociationMedia(Long teachPlanId, String mediaId) {

    }
}
