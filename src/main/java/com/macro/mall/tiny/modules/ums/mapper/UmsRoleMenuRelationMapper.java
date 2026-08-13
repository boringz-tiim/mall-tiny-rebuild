package com.macro.mall.tiny.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.tiny.modules.ums.model.UmsMenu;
import com.macro.mall.tiny.modules.ums.model.UmsRoleMenuRelation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
/**
 * 角色与菜单关系数据库访问接口。
 *
 * 负责维护角色菜单关系，并查询指定角色拥有的菜单。
 */
public interface UmsRoleMenuRelationMapper extends
        BaseMapper<UmsRoleMenuRelation>
{
    /**
     * 查询指定角色拥有的菜单。
     *
     * @param roleId 角色ID
     * @return 角色拥有的菜单列表
     */
    @Select("""
            SELECT menu.*
            FROM ums_menu menu
            INNER JOIN ums_role_menu_relation relation
                ON relation.menu_id = menu.id
            WHERE relation.role_id = #{roleId}
            ORDER BY menu.sort ASC, menu.id ASC
            """)
    List<UmsMenu> selectMenuListByRoleId(
            @Param("roleId") Long roleId
    );
}
