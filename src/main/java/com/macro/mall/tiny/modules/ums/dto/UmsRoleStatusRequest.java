package com.macro.mall.tiny.modules.ums.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 角色状态修改请求DTO
 * 修改角色状态请求
 * 0：禁用
 * 1:启用
 */
public record UmsRoleStatusRequest(
        @NotNull(message = "角色状态不能为空")
        @Min(value = 0, message = "角色状态只能是0或1")
        @Max(value = 1, message = "角色状态只能是0或1")
        Integer status
) {

}
