package com.macro.mall.tiny.security;

public record JwtAdminPrincipal(//这个对象表示当前请求的登录用户，但不保存密码
        Long adminId,
        String username
) {
}
