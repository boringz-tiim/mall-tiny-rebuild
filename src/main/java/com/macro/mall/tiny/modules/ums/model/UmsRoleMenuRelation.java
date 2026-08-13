package com.macro.mall.tiny.modules.ums.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色与菜单关系实体。
 *
 * 对应 ums_role_menu_relation 表，一条记录表示一个角色拥有一个菜单。
 */
@Data
@TableName("ums_role_menu_relation")
public class UmsRoleMenuRelation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色ID。
     */
    private Long roleId;

    /**
     * 菜单ID。
     */
    private Long menuId;
}