package com.macro.mall.tiny.modules.ums.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 接口资源新增和修改请求。
 *
 * @param name 资源名称
 * @param url 资源URL模式
 * @param description 资源说明
 * @param categoryId 所属资源分类ID
 */
public record UmsResourceRequest(
        @NotBlank(message = "资源名称不能为空")
        @Size(max = 200, message = "资源名称不能超过200个字符")
        String name,

        @NotBlank(message = "资源URL不能为空")
        @Size(max = 200, message = "资源URL不能超过200个字符")
        String url,

        @Size(max = 500, message = "资源说明不能超过500个字符")
        String description,

        @NotNull(message = "资源分类ID不能为空")
        @Min(value = 1, message = "资源分类ID必须大于等于1")
        Long categoryId
) {
}