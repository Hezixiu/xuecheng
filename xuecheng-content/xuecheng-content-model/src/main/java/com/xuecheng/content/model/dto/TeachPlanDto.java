package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

/**
 * @author Linzkr
 * @description: TODO
 * @date 2023/1/15 15:46
 */
@Data
public class TeachPlanDto extends Teachplan {
//    关联的媒体资料信息
    TeachplanMedia teachplanMedia;
//    子目录
    List<TeachPlanDto> teachPlanTreeNodes;
}
