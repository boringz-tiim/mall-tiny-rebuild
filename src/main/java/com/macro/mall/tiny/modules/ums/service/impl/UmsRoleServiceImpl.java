package com.macro.mall.tiny.modules.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.macro.mall.tiny.modules.ums.mapper.UmsRoleMapper;
import com.macro.mall.tiny.modules.ums.model.UmsRole;
import com.macro.mall.tiny.modules.ums.service.UmsRoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UmsRoleServiceImpl extends ServiceImpl<UmsRoleMapper, UmsRole>
        implements UmsRoleService {
    @Override
    public Page<UmsRole> list(String keyword, long pageSize, long pageNum) {

        Page<UmsRole>page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<UmsRole> wrapper =
                Wrappers.lambdaQuery(UmsRole.class);

        if (StringUtils.hasText(keyword)) {
            wrapper.like(UmsRole::getName, keyword);
        }

        wrapper
                .orderByAsc(UmsRole::getSort)
                .orderByAsc(UmsRole::getId);

        return page(page, wrapper);
    }
}
