package com.macro.mall.tiny.modules.ums.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * 给后台用户分配角色的请求。
 *
 * @param roleIds 角色ID列表；空列表表示清空该用户的全部角色
 */
public record UmsAdminRoleRequest(
        @NotNull(message = "角色ID列表不能为空")
        List<
                @NotNull(message = "角色ID不能为空")
                @Positive(message = "角色ID必须大于0")
                        Long
                > roleIds
) {
}