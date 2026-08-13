package com.macro.mall.tiny.modules.ums.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 接口资源分类新增和修改请求
 *
 * @param name 分类名称
 * @param sort 排序值，数值越小越靠前
 */
public record UmsResourceCategoryRequest(
        @NotBlank(message = "资源分类名称不能为空")
        @Size(max = 200, message = "资源分类名称不能超过200个字符")
        String name,

        @NotNull(message = "排序值不能为空")
        @Min(value = 0, message = "排序值不能小于0")
        Integer sort
) {
}
