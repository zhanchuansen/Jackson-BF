package com.chuansen.entity.dto;


import com.chuansen.annotation.Query;
import lombok.Data;

/**
 * 日志查询类
 */
@Data
public class LogQueryCriteria {

    // 多字段模糊
    @Query(blurry = "username,description,address,requestIp,method,params")
    private String blurry;

    @Query
    private String logType;
}
