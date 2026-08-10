package com.macro.mall.tiny.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface  UmsAdminMapper extends BaseMapper<UmsAdmin> {
    /**
     * 删除指定用户的角色关联记录
     *
     * @param id
     */
//    @Delete("""delete from ums_admin_role_relation where admin_id = #{adminId}""")
//    void deleteRoleRelationByAdminId(Long adminId);
    @Delete("""
        DELETE FROM ums_admin_role_relation
        WHERE admin_id = #{adminId}
        """)
    int deleteRoleRelationsByAdminId(
            @Param("adminId") Long adminId
    );
}
