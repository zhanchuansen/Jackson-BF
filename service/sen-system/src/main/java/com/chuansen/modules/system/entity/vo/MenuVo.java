package com.chuansen.modules.system.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 构建前端路由时用到
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MenuVo implements Serializable {

    private String name;//一级菜单名称

    private String path;//路径

    private Boolean hidden;//是否隐藏

    private String redirect;

    private String component;

    private Boolean alwaysShow;

    private MenuMetaVo meta;//图标

    private List<MenuVo> children;//二级菜单
}
