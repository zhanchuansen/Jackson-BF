package com.chuansen.modules.system.entity.dto;

import lombok.Data;
import com.chuansen.annotation.Query;

/**
 * 公共查询类
 */
@Data
public class MenuQueryCriteria {

    // 多字段模糊
    @Query(blurry = "name,path,component")
    private String blurry;
}
