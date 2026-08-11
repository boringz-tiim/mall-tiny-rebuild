package com.macro.mall.tiny.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.tiny.modules.ums.model.UmsRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;


/**
 * 后台角色Mapper
 */
public interface UmsRoleMapper extends BaseMapper <UmsRole> {
    /**
     * 删除角色与后台用户的关系
     * @param id
     */
    @Delete("""
DELETE FROM ums_admin_role_relation WHERE role_id=#{roleId}
""")
    int deleteAdminRelationsByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除角色与菜单的关系
     * @param id
     */
    @Delete("""
DELETE FROM ums_role_menu_relation WHERE role_id=#{roleId}
""")
    int deleteMenuRelationsByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除角色与接口资源的关系。
     */
    @Delete("""
        DELETE FROM ums_role_resource_relation
        WHERE role_id = #{roleId}
        """)
    int deleteResourceRelationsByRoleId(
            @Param("roleId") Long roleId);
}
