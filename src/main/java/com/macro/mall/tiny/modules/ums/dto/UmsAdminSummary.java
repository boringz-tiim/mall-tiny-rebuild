package com.macro.mall.tiny.modules.ums.dto;

import com.macro.mall.tiny.modules.ums.model.UmsAdmin;

import java.time.LocalDateTime;
//Java17 的record写法用来定义一个只负责承载数据的DTO
//相当于一个不可变数据类，java会自动生成全参数构造函数，所有字段的访问方法，
//一个不包含密码的摘要DTO UmsAdminSummary
public record UmsAdminSummary (
        Long id,
        String username,
        String icon,
        String email,
        String nickName,
        String note,
        LocalDateTime createTime,
        LocalDateTime loginTime,
        Integer status
){
    public static UmsAdminSummary from(UmsAdmin admin){
        return new UmsAdminSummary(
                admin.getId(),
                admin.getUsername(),
                admin.getIcon(),
                admin.getEmail(),
                admin.getNickName(),
                admin.getNote(),
                admin.getCreateTime(),
                admin.getLoginTime(),
                admin.getStatus()
        );
    }


}
