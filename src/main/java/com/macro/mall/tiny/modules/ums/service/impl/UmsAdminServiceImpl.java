package com.macro.mall.tiny.modules.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.macro.mall.tiny.common.exception.ApiException;
import com.macro.mall.tiny.modules.ums.mapper.UmsAdminMapper;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;
import com.macro.mall.tiny.modules.ums.service.UmsAdminService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Provider;
import java.util.List;
@Service
public class UmsAdminServiceImpl
        extends ServiceImpl<UmsAdminMapper,UmsAdmin>
        implements UmsAdminService {
    @Override
    public Page<UmsAdmin> list(String keyword, long pageSize, long pageNum) {
        Page<UmsAdmin> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<UmsAdmin> wrapper =
                Wrappers.lambdaQuery(UmsAdmin.class);

        if (StringUtils.hasText(keyword)) {
            wrapper.and(query -> query
                    .like(UmsAdmin::getUsername, keyword)
                    .or()
                    .like(UmsAdmin::getNickName, keyword)
            );
        }

        wrapper.orderByAsc(UmsAdmin::getId);

        return page(page, wrapper);
    }

    @Override
    public UmsAdmin getDetail(Long id) {
        UmsAdmin admin = getById(id);
        if(admin==null){
            throw new ApiException("用户不存在");
        }
        return admin;
    }
}
