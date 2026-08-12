package com.macro.mall.tiny.modules.ums.dto;

import com.macro.mall.tiny.modules.ums.model.UmsMenu;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单树节点响应。
 *
 * 在菜单基本信息基础上增加 children 字段。
 */
public record UmsMenuNode(
        Long id,
        Long parentId,
        LocalDateTime createTime,
        String title,
        Integer level,
        Integer sort,
        String name,
        String icon,
        Integer hidden,
        List<UmsMenuNode> children
) {

    /**
     * 将菜单实体转换为菜单树节点。
     */
    public static UmsMenuNode from(
            UmsMenu menu,
            List<UmsMenuNode> children
    ) {
        return new UmsMenuNode(
                menu.getId(),
                menu.getParentId(),
                menu.getCreateTime(),
                menu.getTitle(),
                menu.getLevel(),
                menu.getSort(),
                menu.getName(),
                menu.getIcon(),
                menu.getHidden(),
                children
        );
    }
}