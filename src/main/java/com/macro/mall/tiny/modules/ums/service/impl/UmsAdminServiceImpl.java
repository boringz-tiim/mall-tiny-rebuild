package com.macro.mall.tiny.modules.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.macro.mall.tiny.common.exception.ApiException;
import com.macro.mall.tiny.modules.ums.dto.UmsAdminCreateRequest;
import com.macro.mall.tiny.modules.ums.mapper.UmsAdminMapper;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;
import com.macro.mall.tiny.modules.ums.service.UmsAdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;



import java.time.LocalDateTime;

@Service
public class UmsAdminServiceImpl
        extends ServiceImpl<UmsAdminMapper,UmsAdmin>
        implements UmsAdminService {
    //在类中添加成员变量和构造器
    private final PasswordEncoder passwordEncoder;


    public UmsAdminServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;

    }

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
        if (admin == null) {
            throw new ApiException("用户不存在");
        }
        return admin;
    }

    /**
     * 创建新用户
     *
     * @param umsAdminCreateRequest
     * @return
     */
    @Override
    public UmsAdmin create(UmsAdminCreateRequest umsAdminCreateRequest) {
        long sameUsernameCount = count(Wrappers.<UmsAdmin>lambdaQuery().eq(UmsAdmin::getUsername, umsAdminCreateRequest.username()));
        if (sameUsernameCount > 0) {
            throw new ApiException("用户已存在");

        }
        UmsAdmin admin = new UmsAdmin();
        admin.setUsername(umsAdminCreateRequest.username());
        admin.setPassword(passwordEncoder.encode(umsAdminCreateRequest.password()));
        admin.setIcon(umsAdminCreateRequest.icon());
        admin.setEmail(umsAdminCreateRequest.email());
        admin.setNickName(umsAdminCreateRequest.nickName());
        admin.setNote(umsAdminCreateRequest.note());
        admin.setCreateTime(LocalDateTime.now());
        admin.setStatus(1);
        boolean saved = save(admin);
        if (!saved) {
            throw new ApiException("创建用户失败");
        }
        return admin;
    }
}