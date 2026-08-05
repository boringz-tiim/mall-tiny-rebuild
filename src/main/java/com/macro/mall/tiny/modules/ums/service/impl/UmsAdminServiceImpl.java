package com.macro.mall.tiny.modules.ums.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.macro.mall.tiny.common.exception.ApiException;
import com.macro.mall.tiny.modules.ums.dto.UmsAdminCreateRequest;
import com.macro.mall.tiny.modules.ums.dto.UmsAdminLoginRequest;
import com.macro.mall.tiny.modules.ums.mapper.UmsAdminMapper;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;
import com.macro.mall.tiny.modules.ums.service.UmsAdminService;
import com.macro.mall.tiny.security.JwtTokenService;
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
    private final JwtTokenService jwtTokenService;

    public UmsAdminServiceImpl(PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService=jwtTokenService;

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

    /**
     * 根据用户名查询后台用户
     * @param username
     * @return
     */
    @Override
    public UmsAdmin getByUsername(String username) {
        return getOne(
                Wrappers.<UmsAdmin>lambdaQuery()
                        .eq(
                                UmsAdmin::getUsername,
                                username
                        )
        );
    }

    @Override
    public UmsAdmin authenticate(UmsAdminLoginRequest request) {
        UmsAdmin admin = getByUsername(request.username());
        if(admin==null || admin.getPassword()==null||!passwordEncoder.matches(request.password(),admin.getPassword())){
            throw new ApiException("用户名或密码错误");
        }
        if(!Integer.valueOf(1).equals(admin.getStatus())){
            throw new ApiException("账号已被禁用");
        }
        return admin;
    }

    /**
     * 实现登录方法
     * @param request
     * @return
     */
    @Override
    public String login(UmsAdminLoginRequest request) {
        UmsAdmin admin = authenticate(request);
        return jwtTokenService.generateToken(admin);
    }
}