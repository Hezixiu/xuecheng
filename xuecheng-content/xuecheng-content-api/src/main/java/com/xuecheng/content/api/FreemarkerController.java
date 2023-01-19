package com.xuecheng.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Linzkr
 * @description: TODO 测试FreeMarker
 * @date 2023/1/19 13:48
 */
@Controller  //因为Freemarker返回的是页面而不是json 所以用Controller注解不用RestController
public class FreemarkerController {
    @GetMapping("/testfreemarker")
    public ModelAndView test(){
        ModelAndView modelAndView = new ModelAndView();
//        准备模型数据
        modelAndView.addObject("name","小明");
//        设置视图的名称，就是模板文件的名称 不包括后缀名
        modelAndView.setViewName("test");
        return modelAndView;
    }
}
