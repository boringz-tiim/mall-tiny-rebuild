package com.macro.mall.tiny.modules.ums.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 后台用户与角色关系实体
 *
 * 对应Ums_admin_role_relation表，一条记录表示一个用户拥有一个角色
 */
@Data
@TableName("ums_admin_role_relation")
public class UmsAdminRoleRelation {
    @TableId(value="id",type= IdType.AUTO)
    private Long id;
    /**
     * 后台用户ID
     */
    private Long adminId;
    /**
     * 角色ID
     */
    private Long roleId;
}
