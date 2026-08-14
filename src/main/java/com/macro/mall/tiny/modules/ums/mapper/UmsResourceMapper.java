package com.macro.mall.tiny.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.tiny.modules.ums.model.UmsResource;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * 后台接口资源数据库访问接口。
 *
 * 继承 MyBatis-Plus BaseMapper，提供接口资源基础 CRUD 能力。
 */
public interface UmsResourceMapper
        extends BaseMapper<UmsResource> {
    /**
     * 删除指定资源的角色资源关联记录。
     *
     * @param resourceId 资源ID
     * @return 删除的关联记录数量
     */
    @Delete("""
            delete from ums_role_resource_relation
            where resource_id = #{resourceId}
            """)
    int deleteRoleRelationsByResourceId(
            @Param("resourceId") Long resourceId
    );
}