package com.macro.mall.tiny.modules.ums.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * 新增角色请求。
 *
 * 客户端不能提交 id、adminCount、createTime 和 status。
 */
public record UmsRoleCreateRequest (
        @NotBlank(message="角色名称不能为空")
        @Size(max=100,message="角色名称不能超过100个字符")
        String name,
        @Size(max = 500,message = "角色描述不能超过500个字符")
        String description,
        @Min(value=0,message="排序值不能小于0")
        Integer sort
){

}
