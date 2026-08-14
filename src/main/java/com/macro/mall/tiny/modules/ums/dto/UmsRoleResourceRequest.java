package com.macro.mall.tiny.modules.ums.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * 给角色分配接口资源的请求。
 *
 * @param resourceIds 资源ID列表；空列表表示清空角色的全部资源
 */
public record UmsRoleResourceRequest(
        @NotNull(message = "资源ID列表不能为空")
        List<
                @NotNull(message = "资源ID不能为空")
                @Positive(message = "资源ID必须大于0")
                        Long
                > resourceIds
) {
}