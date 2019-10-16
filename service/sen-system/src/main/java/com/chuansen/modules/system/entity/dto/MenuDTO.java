package com.chuansen.modules.system.entity.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 往数据库存的信息
 */
@Data
public class MenuDTO {

    private Long id;

    private String name;

    private Long sort;

    private String path;

    private String component;

    private Long pid;

    private Boolean iFrame;

    private Boolean cache;

    private Boolean hidden;

    private String componentName;

    private String icon;

    private List<MenuDTO> children;

    private Timestamp createTime;
}
