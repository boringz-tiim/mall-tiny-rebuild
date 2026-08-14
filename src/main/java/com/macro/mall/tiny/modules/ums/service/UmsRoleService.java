package com.macro.mall.tiny.modules.ums.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.macro.mall.tiny.modules.ums.dto.UmsRoleCreateRequest;
import com.macro.mall.tiny.modules.ums.dto.UmsRoleUpdateRequest;
import com.macro.mall.tiny.modules.ums.model.UmsMenu;
import com.macro.mall.tiny.modules.ums.model.UmsResource;
import com.macro.mall.tiny.modules.ums.model.UmsRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 后台角色业务接口
 *
 * 定义角色查询，新增，修改，状态管理和删除等业务能力
 */
public interface UmsRoleService extends IService<UmsRole>{
/**
 * 根据角色名称分页查询角色
 */
Page<UmsRole> list(String keyword,long pageSize,long pageNum);

    /**
     * 创建新的角色
     * @param request
     * @return
     */
    UmsRole create( UmsRoleCreateRequest request);

    /**
     * 根据Id查询角色详情
     * @param id
     * @return
     */
    UmsRole getDetail(Long id);
    UmsRole update(Long id, UmsRoleUpdateRequest request);

    UmsRole updateStatus( Long id,  Integer status);

    void deleteRole(Long id);
    /**
     * 查询指定角色拥有的菜单。
     *
     * @param roleId 角色ID
     * @return 角色拥有的菜单列表
     */
    List<UmsMenu> getMenuList(Long roleId);
    /**
     * 重新分配指定角色的菜单
     * @param roleId 角色Id
     * @param menuIds 新的菜单ID列表，空列表表示清空全部菜单
     * @return 分配完成后的菜单列表
     */
    List<UmsMenu> updateMenus(Long roleId,List<Long>menuIds);
    /**
     * 查询指定角色拥有的接口资源。
     *
     * @param roleId 角色ID
     * @return 角色拥有的接口资源列表
     */
    List<UmsResource> getResourceList(Long roleId);

    /**
     * 重新分配指定角色的接口资源。
     *
     * @param roleId 角色ID
     * @param resourceIds 新的资源ID列表，空列表表示清空全部资源
     * @return 分配完成后的接口资源列表
     */
    List<UmsResource> updateResources(
            Long roleId,
            List<Long> resourceIds
    );
}
