package com.macro.mall.tiny.modules.ums.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

//定义后台用户实体类，让java对象与数据库中的ums_admin表建立映射
@Data
@TableName("ums_admin")    //告诉MyBatis-Plus，该java类映射到ums_admin表
public class UmsAdmin {
    //主键列名为id，id由MySQL的自增机制生成
    @TableId(value = "id",type= IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String icon;
    private String email;
    private String nickName;
    private String note;
    private LocalDateTime createTime;
    private LocalDateTime loginTime;
    private Integer status;
}
