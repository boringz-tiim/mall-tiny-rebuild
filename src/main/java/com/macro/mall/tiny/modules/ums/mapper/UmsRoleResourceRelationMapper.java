package com.macro.mall.tiny.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.tiny.modules.ums.model.UmsResource;
import com.macro.mall.tiny.modules.ums.model.UmsRoleResourceRelation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色与接口资源关系数据库访问接口。
 *
 * 负责维护角色资源关系，并查询指定角色拥有的接口资源。
 */
public interface UmsRoleResourceRelationMapper
        extends BaseMapper<UmsRoleResourceRelation> {

    /**
     * 查询指定角色拥有的接口资源。
     *
     * @param roleId 角色ID
     * @return 角色拥有的接口资源列表
     */
    @Select("""
            select resource.*
            from ums_resource resource
            inner join ums_role_resource_relation relation
                on relation.resource_id = resource.id
            where relation.role_id = #{roleId}
            order by resource.category_id asc, resource.id asc
            """)
    List<UmsResource> selectResourceListByRoleId(
            @Param("roleId") Long roleId
    );
}