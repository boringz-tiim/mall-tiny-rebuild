package com.macro.mall.tiny.modules.ums.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 修改后台用户基本信息的请求
 * 不允许通过此接口修改用户名、密码和账号状态
 * 这些字段由各自的专用接口处理
 */
public record UmsAdminUpdateRequest(
        @Size(max=500,message="头像地址不能超过500个字符") String icon,
        @Email(message="邮箱地址不正确")
        @Size(max = 100,message = "邮箱不能超过100个字符")
        String email,
        @Size(max = 200, message = "昵称不能超过200个字符")
        String nickName,

        @Size(max = 500, message = "备注不能超过500个字符")
        String note

) {
}
