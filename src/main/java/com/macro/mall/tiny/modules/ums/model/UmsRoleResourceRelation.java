package com.macro.mall.tiny.modules.ums.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色与接口资源关系实体。
 *
 * 对应 ums_role_resource_relation 表，
 * 一条记录表示一个角色拥有一个接口资源。
 */
@Data
@TableName("ums_role_resource_relation")
public class UmsRoleResourceRelation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色ID。
     */
    private Long roleId;

    /**
     * 接口资源ID。
     */
    private Long resourceId;
}