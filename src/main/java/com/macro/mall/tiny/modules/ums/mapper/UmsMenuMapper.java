package com.macro.mall.tiny.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.tiny.modules.ums.model.UmsMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * 后台菜单数据库访问接口
 *
 * 继承MyBatis-Plus BaseMapper,提供菜单基础CRUD能力
 */
public interface UmsMenuMapper extends BaseMapper <UmsMenu>{
    /**
     * 删除指定菜单的角色菜单关系
     */
    @Delete("""
            delete from ums_role_menu_relation
            where menu_id=#{menuId}
            """)
    int deleteRoleRelationsByMenuId(@Param("menuId") Long menuId );
}
