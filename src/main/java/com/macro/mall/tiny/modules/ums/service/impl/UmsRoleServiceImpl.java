package com.macro.mall.tiny.modules.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.macro.mall.tiny.common.exception.ApiException;
import com.macro.mall.tiny.modules.ums.dto.UmsRoleCreateRequest;
import com.macro.mall.tiny.modules.ums.dto.UmsRoleUpdateRequest;
import com.macro.mall.tiny.modules.ums.mapper.UmsRoleMapper;
import com.macro.mall.tiny.modules.ums.model.UmsRole;
import com.macro.mall.tiny.modules.ums.service.UmsRoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 后台角色业务实现类
 * 负责角色数据校验，状态修改，以及删除角色时的关联关系清理
 */
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

    /**
     * 创建新角色
     * @param request
     * @return
     */
    @Override
    public UmsRole create(UmsRoleCreateRequest request) {
        long sameNameCount=count(
                Wrappers.<UmsRole> lambdaQuery()
                        .eq(UmsRole::getName,request.name())
        );

        if(sameNameCount>0){
            throw new ApiException("角色名称已经存在");
        }
        UmsRole role = new UmsRole();
        role.setName(request.name());
        role.setDescription(request.description());
        role.setAdminCount(0);
        role.setCreateTime(LocalDateTime.now());
        role.setStatus(1);
        role.setSort(request.sort() == null ? 0 : request.sort());

        boolean saved = save(role);

        if (!saved) {
            throw new ApiException("创建角色失败");
        }

        return role;

    }

    @Override
    public UmsRole getDetail(Long id) {
        UmsRole role = getById(id);
        if(role==null){
            throw new ApiException("角色不存在");
        }
        return role;
    }

    @Override
    public UmsRole update(Long id, UmsRoleUpdateRequest request) {
        UmsRole role = getDetail(id);
        long sameNameCount=count(
                Wrappers.<UmsRole>lambdaQuery()
                        .eq(UmsRole::getName,request.name())
                        .ne(UmsRole::getId,id)
        );
        if(sameNameCount>0){
            throw new ApiException("角色名称已存在");
        }
        boolean updated = update(
                Wrappers.<UmsRole>lambdaUpdate()
                        .eq(UmsRole::getId, id)
                        .set(UmsRole::getName, request.name())
                        .set(UmsRole::getDescription, request.description())
                        .set(UmsRole::getSort, request.sort())
        );

        if (!updated) {
            throw new ApiException("修改角色失败");
        }

        role.setName(request.name());
        role.setDescription(request.description());
        role.setSort(request.sort());

        return role;
    }

    @Override
    public UmsRole updateStatus(Long id, Integer status) {
        UmsRole role = getDetail(id);
        boolean updated=update(
                Wrappers.<UmsRole>lambdaUpdate()
                        .eq(UmsRole::getId,id)
                        .set(UmsRole::getStatus,status)
        );
        if(!updated){
            throw new ApiException("修改角色状态失败");
        }
        role.setStatus(status);
        return role;
    }

    @Override
    @Transactional

    public void deleteRole(Long id) {
        getDetail(id);
        baseMapper.deleteAdminRelationsByRoleId(id);
        baseMapper.deleteMenuRelationsByRoleId(id);
        baseMapper.deleteResourceRelationsByRoleId(id);
        boolean removed = removeById(id);
        if (!removed) {
            throw new ApiException("删除角色失败");
        }
    }



}
