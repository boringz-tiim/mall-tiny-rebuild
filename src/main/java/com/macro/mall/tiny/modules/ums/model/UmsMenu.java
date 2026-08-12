package com.macro.mall.tiny.modules.ums.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台菜单实体。
 *
 * 对应 ums_menu 表，使用 parentId 表示菜单之间的父子关系。
 */
@Data
@TableName("ums_menu")
public class UmsMenu {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父菜单ID，0表示顶级菜单。
     */
    private Long parentId;

    private LocalDateTime createTime;

    /**
     * 后台页面显示的菜单标题。
     */
    private String title;

    /**
     * 菜单层级，0表示顶级菜单。
     */
    private Integer level;

    /**
     * 排序值，数值越小越靠前。
     */
    private Integer sort;

    /**
     * 前端路由或组件使用的菜单名称。
     */
    private String name;

    /**
     * 前端显示的图标名称。
     */
    private String icon;

    /**
     * 是否隐藏：0表示显示，1表示隐藏。
     */
    private Integer hidden;
}