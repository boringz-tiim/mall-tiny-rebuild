package com.macro.mall.tiny.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.tiny.modules.ums.model.UmsAdminRoleRelation;
import com.macro.mall.tiny.modules.ums.model.UmsResource;
import com.macro.mall.tiny.modules.ums.model.UmsRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 后台用户与角色关系数据库访问接口。
 *
 * 负责维护用户角色关系，并查询指定用户拥有的角色。
 */
public interface UmsAdminRoleRelationMapper
        extends BaseMapper<UmsAdminRoleRelation> {

    /**
     * 查询指定后台用户拥有的角色。
     *
     * @param adminId 后台用户ID
     * @return 用户拥有的角色列表
     */
    @Select("""
            SELECT r.*
            FROM ums_role r
            INNER JOIN ums_admin_role_relation relation
                ON relation.role_id = r.id
            WHERE relation.admin_id = #{adminId}
            ORDER BY r.sort ASC, r.id ASC
            """)
    List<UmsRole> selectRoleListByAdminId(
            @Param("adminId") Long adminId
    );
    /**
     * 查询指定后台用户通过启用角色获得的接口资源。
     *
     * 多个角色拥有同一资源时，通过 DISTINCT 去除重复记录。
     *
     * @param adminId 后台用户ID
     * @return 用户拥有的接口资源列表
     */
    @Select("""
        select distinct resource.*
        from ums_resource resource
        inner join ums_role_resource_relation role_resource
            on role_resource.resource_id = resource.id
        inner join ums_role role
            on role.id = role_resource.role_id
        inner join ums_admin_role_relation admin_role
            on admin_role.role_id = role.id
        where admin_role.admin_id = #{adminId}
          and role.status = 1
        order by resource.category_id asc, resource.id asc
        """)
    List<UmsResource> selectResourceListByAdminId(
            @Param("adminId") Long adminId
    );
}