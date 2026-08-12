package com.macro.mall.tiny.modules.ums.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.macro.mall.tiny.common.exception.ApiException;
import com.macro.mall.tiny.modules.ums.dto.UmsMenuNode;
import com.macro.mall.tiny.modules.ums.dto.UmsMenuRequest;
import com.macro.mall.tiny.modules.ums.mapper.UmsMenuMapper;
import com.macro.mall.tiny.modules.ums.model.UmsMenu;
import com.macro.mall.tiny.modules.ums.service.UmsMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台菜单业务实现类
 *
 * 负责菜单查询预计后续菜单层级和CRUD业务处理
 */
@Service
public class UmsMenuServiceImpl extends ServiceImpl<UmsMenuMapper, UmsMenu >
    implements UmsMenuService {

    @Override
    public List<UmsMenu> listByParentId(Long parentId) {
        return list(
                Wrappers.<UmsMenu> lambdaQuery()
                        .eq(UmsMenu::getParentId,parentId)
                        .orderByAsc(UmsMenu::getSort,UmsMenu::getId)
        );
    }

    /**
     * 根据父菜单计算当前菜单层级
     * @param parentId
     * @return
     */
    private Integer calculateLevel(Long parentId){
        if(parentId==0L){
            return 0;
        }
        UmsMenu parentMenu=getDetail(parentId);
        return parentMenu.getLevel()+1;
    }
    @Override
    public UmsMenu getDetail(Long id) {
        UmsMenu menu = getById(id);
        if(menu==null){
            throw new ApiException("菜单不存在");
        }
        return menu;
    }

    @Override
    public UmsMenu create(UmsMenuRequest request) {
        UmsMenu menu = new UmsMenu();

        menu.setParentId(request.parentId());
        menu.setCreateTime(LocalDateTime.now());
        menu.setTitle(request.title());
        menu.setLevel(calculateLevel(request.parentId()));
        menu.setSort(request.sort());
        menu.setName(request.name());
        menu.setIcon(request.icon());
        menu.setHidden(request.hidden());

        boolean saved = save(menu);

        if (!saved) {
            throw new ApiException("创建菜单失败");
        }

        return menu;
    }

    @Override
    public UmsMenu update(Long id, UmsMenuRequest request) {
        UmsMenu menu = getDetail(id);

        checkParentCycle(id, request.parentId());

        Integer level = calculateLevel(request.parentId());

        boolean updated = update(
                Wrappers.<UmsMenu>lambdaUpdate()
                        .eq(UmsMenu::getId, id)
                        .set(UmsMenu::getParentId, request.parentId())
                        .set(UmsMenu::getTitle, request.title())
                        .set(UmsMenu::getLevel, level)
                        .set(UmsMenu::getSort, request.sort())
                        .set(UmsMenu::getName, request.name())
                        .set(UmsMenu::getIcon, request.icon())
                        .set(UmsMenu::getHidden, request.hidden())
        );

        if (!updated) {
            throw new ApiException("修改菜单失败");
        }

        menu.setParentId(request.parentId());
        menu.setTitle(request.title());
        menu.setLevel(level);
        menu.setSort(request.sort());
        menu.setName(request.name());
        menu.setIcon(request.icon());
        menu.setHidden(request.hidden());

        return menu;
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        getDetail(id);
        long childCount=count(
                Wrappers.<UmsMenu>lambdaQuery()
                        .eq(UmsMenu::getParentId,id)
        );
        if(childCount >0){
            throw new ApiException("当前菜单存在子菜单，不能删除");
        }
        baseMapper.deleteRoleRelationsByMenuId(id);
        boolean removed = removeById(id);

        if (!removed) {
            throw new ApiException("删除菜单失败");
        }
    }

    @Override
    public List<UmsMenuNode> treeList() {
        List<UmsMenu> menus = list(
                Wrappers.<UmsMenu>lambdaQuery()
                        .orderByAsc(
                                UmsMenu::getSort,
                                UmsMenu::getId
                        )

        );
        Map<Long, List<UmsMenu>> childrenMap =//自动把每个UmsMenu的parentId作为Map的key,而getParentId()返回值的类型就是Long
                menus.stream()
                        .collect(
                                Collectors.groupingBy(
                                        UmsMenu::getParentId
                                )
                        );

        return buildChildren(0L, childrenMap);
    }
    /**
     * 递归构造指定父菜单下的子树。
     */
    private List<UmsMenuNode> buildChildren(
            Long parentId,
            Map<Long, List<UmsMenu>> childrenMap
    ) {
        return childrenMap
                .getOrDefault(parentId, List.of())
                .stream()
                .map(menu -> UmsMenuNode.from(
                        menu,
                        buildChildren(
                                menu.getId(),
                                childrenMap
                        )
                ))
                .toList();
    }
    /**
     * 检查把菜单移动到指定父菜单下是否会形成循环。
     */
    private void checkParentCycle(
            Long menuId,
            Long parentId
    ) {
        Long currentParentId = parentId;

        while (!Long.valueOf(0L).equals(currentParentId)) {
            if (menuId.equals(currentParentId)) {
                throw new ApiException("不能将菜单移动到自己的子菜单下");
            }

            UmsMenu parentMenu = getDetail(currentParentId);
            currentParentId = parentMenu.getParentId();
        }
    }
}



