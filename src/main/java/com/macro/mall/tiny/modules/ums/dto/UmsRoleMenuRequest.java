package com.macro.mall.tiny.modules.ums.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * 给角色分配菜单的请求。
 *
 * @param menuIds 菜单ID列表；空列表表示清空角色的全部菜单
 */
public record UmsRoleMenuRequest(
        @NotNull(message="菜单ID列表不能为空")
        List<@NotNull (message="菜单ID不能为空")
                @Positive (message="菜单ID必须大于0")Long>menuIds
) {
}
