package com.macro.mall.tiny.modules.ums.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.macro.mall.tiny.modules.ums.dto.UmsMenuNode;
import com.macro.mall.tiny.modules.ums.dto.UmsMenuRequest;
import com.macro.mall.tiny.modules.ums.model.UmsMenu;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

/**
 * 后台菜单业务接口。
 *
 * 定义菜单查询和后续菜单管理业务。
 */
public interface UmsMenuService extends IService<UmsMenu> {

    /**
     * 查询指定父菜单下的直接子菜单。
     *
     * @param parentId 父菜单ID，0表示查询顶级菜单
     * @return 直接子菜单列表
     */
    List<UmsMenu> listByParentId(Long parentId);

    /**
     * 根据ID查询菜单详情
     * @param id
     * @return
     */
    UmsMenu getDetail(Long id);

    /**
     * 创建菜单
     * @param request
     * @return
     */
    UmsMenu create( UmsMenuRequest request);

    /**
     * 修改菜单
     * @param id
     * @param request
     * @return
     */
    UmsMenu update(@Min(value = 1,message = "菜单ID必须大于等于1") Long id, @Valid UmsMenuRequest request);
    /**
     * 删除菜单
     *
     * 有子菜单时禁止删除，删除时清楚角色菜单关系
     *
     * @param id 菜单ID
     */
    void deleteMenu(Long id);
    /**
     * 查询并构造完整菜单树
     *
     * @return 从顶级菜单开始的菜单树
     */
    List<UmsMenuNode> treeList();
}