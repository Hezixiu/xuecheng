package com.xuecheng.base.model;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
//查询的所需类
public class PageParams {
//    当前页面的默认值
    public static final long DEFAULT_PAGE_CURRENT = 1L;
    //    每页记录数的默认值
    public static final long DEFAULT_PAGE_SIZE =10L;

    @ApiModelProperty("当前页码")
//    当前页码
    public Long pageCurrent = DEFAULT_PAGE_CURRENT;

    @ApiModelProperty("每页显示记录数")
//    每页记录数的默认值
    public Long pageSize = DEFAULT_PAGE_SIZE;

}
