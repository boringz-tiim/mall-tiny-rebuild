package com.macro.mall.tiny.modules.ums.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UmsAdminPasswordRequest (
        @NotBlank(message="密码不能为空")
        @Size(min=6,max=32, message="原密码长度必须在6到32个字符之间")
        String oldPassword,
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6,max = 32,message = "新密码长度必须在6到32个字符之间")
        String newPassword
        ){
}
