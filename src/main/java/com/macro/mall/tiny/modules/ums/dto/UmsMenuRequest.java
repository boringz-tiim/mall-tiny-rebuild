package com.macro.mall.tiny.modules.ums.dto;

import jakarta.validation.constraints.*;

/**
 * 菜单新增和修改请求。
 *
 * @param parentId 父菜单ID，0表示顶级菜单
 * @param title 菜单显示标题
 * @param sort 排序值
 * @param name 前端菜单名称
 * @param icon 前端图标
 * @param hidden 是否隐藏，0表示显示，1表示隐藏
 */
public record UmsMenuRequest(
        @NotNull(message = "父菜单ID不能为空")
        @Min(value = 0, message = "父菜单ID不能小于0")
        Long parentId,

        @NotBlank(message = "菜单标题不能为空")
        @Size(max = 100, message = "菜单标题不能超过100个字符")
        String title,

        @NotNull(message = "排序值不能为空")
        @Min(value = 0, message = "排序值不能小于0")
        Integer sort,

        @NotBlank(message = "前端菜单名称不能为空")
        @Size(max = 100, message = "前端菜单名称不能超过100个字符")
        String name,

        @Size(max = 200, message = "菜单图标不能超过200个字符")
        String icon,

        @NotNull(message = "隐藏状态不能为空")
        @Min(value = 0, message = "隐藏状态只能是0或1")
        @Max(value = 1, message = "隐藏状态只能是0或1")
        Integer hidden
) {
}