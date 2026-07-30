package com.macro.mall.tiny.modules.ums.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UmsAdminCreateRequest(
        @NotBlank(message="用户名不能为空")
        @Size(min=4,max=64,message="用户名长度必须在4到64个字符之间")
        String username,
        @NotBlank(message = "密码不能为空")
        @Size(min=6,max = 32,message = "密码长度必须在6到32个字符之间")
        String password,
        @Size(max=500,message = "头像地址不能超过500个字符")
        String icon,
        @Email(message="邮箱格式不正确")
        @Size(max = 100,message = "邮箱不能超过100个字符")
        String email,
        @Size(max = 200,message = "昵称不能超过200个字符")
        String nickName,
        @Size(max = 500,message = "备注不能超过500个字符")
        String note

) {

}
