package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Linzkr
 * @description: TODO 课程类型分类
 * @date 2023/1/14 21:43
 */
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //      数据库 递归SQL 得出父节点+子节点  （父节点在前 子节点在后）
        List<CourseCategoryTreeDto> categoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        List<CourseCategoryTreeDto> treeNodeResult =new ArrayList<>();
//        为了方便判断是子节点还是父节点 使用一个map
        Map<String , CourseCategoryTreeDto>  hashMap = new HashMap<>();
        categoryTreeDtos.forEach(item->{
            hashMap.put(item.getId(), item);
//            找出了根节点
            if (item.getParentid().equals(id)){
                treeNodeResult.add(item);
            }
            String parentid = item.getParentid();
//            hashMap中有父节点
            if (hashMap.containsKey(parentid)){
//                找到了该子节点的父节点
                CourseCategoryTreeDto parentNode = hashMap.get(parentid);
                List<CourseCategoryTreeDto> childrenTreeNodes = parentNode.getChildrenTreeNodes();
//                父节点还没有添加过子节点  初始化list
                if (childrenTreeNodes==null){
                    parentNode.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
//                父节点添加子节点
                parentNode.getChildrenTreeNodes().add(item);
            }
        });

        return treeNodeResult;
    }

}
