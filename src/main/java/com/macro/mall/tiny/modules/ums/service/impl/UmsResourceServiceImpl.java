package com.macro.mall.tiny.modules.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.macro.mall.tiny.common.exception.ApiException;
import com.macro.mall.tiny.modules.ums.dto.UmsResourceRequest;
import com.macro.mall.tiny.modules.ums.mapper.UmsResourceCategoryMapper;
import com.macro.mall.tiny.modules.ums.mapper.UmsResourceMapper;
import com.macro.mall.tiny.modules.ums.model.UmsResource;
import com.macro.mall.tiny.modules.ums.service.UmsResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 后台接口资源业务实现类。
 *
 * 负责接口资源分页筛选、分类有效性校验和 CRUD 业务处理。
 */
@Service
public class UmsResourceServiceImpl
        extends ServiceImpl<UmsResourceMapper, UmsResource>
        implements UmsResourceService {

    private final UmsResourceCategoryMapper resourceCategoryMapper;

    public UmsResourceServiceImpl(
            UmsResourceCategoryMapper resourceCategoryMapper
    ) {
        this.resourceCategoryMapper = resourceCategoryMapper;
    }

    @Override
    public Page<UmsResource> list(
            Long categoryId,
            String nameKeyword,
            String urlKeyword,
            long pageSize,
            long pageNum
    ) {
        Page<UmsResource> page =
                new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<UmsResource> wrapper =
                Wrappers.lambdaQuery(UmsResource.class);

        if (categoryId != null) {
            wrapper.eq(
                    UmsResource::getCategoryId,
                    categoryId
            );
        }

        if (StringUtils.hasText(nameKeyword)) {
            wrapper.like(
                    UmsResource::getName,
                    nameKeyword
            );
        }

        if (StringUtils.hasText(urlKeyword)) {
            wrapper.like(
                    UmsResource::getUrl,
                    urlKeyword
            );
        }

        wrapper.orderByDesc(UmsResource::getId);

        return page(page, wrapper);
    }

    @Override
    public UmsResource getDetail(Long id) {
        UmsResource resource = getById(id);

        if (resource == null) {
            throw new ApiException("接口资源不存在");
        }

        return resource;
    }

    @Override
    public UmsResource create(
            UmsResourceRequest request
    ) {
        checkCategoryExists(request.categoryId());

        UmsResource resource = new UmsResource();

        resource.setCreateTime(LocalDateTime.now());
        resource.setName(request.name());
        resource.setUrl(request.url());
        resource.setDescription(request.description());
        resource.setCategoryId(request.categoryId());

        boolean saved = save(resource);

        if (!saved) {
            throw new ApiException("创建接口资源失败");
        }

        return resource;
    }

    @Override
    public UmsResource update(
            Long id,
            UmsResourceRequest request
    ) {
        UmsResource resource = getDetail(id);

        checkCategoryExists(request.categoryId());

        resource.setName(request.name());
        resource.setUrl(request.url());
        resource.setDescription(request.description());
        resource.setCategoryId(request.categoryId());

        boolean updated = updateById(resource);

        if (!updated) {
            throw new ApiException("修改接口资源失败");
        }

        return resource;
    }

    @Override
    @Transactional
    public void deleteResource(Long id) {
        getDetail(id);

        baseMapper.deleteRoleRelationsByResourceId(id);

        boolean removed = removeById(id);

        if (!removed) {
            throw new ApiException("删除接口资源失败");
        }
    }

    /**
     * 检查资源分类是否存在。
     *
     * @param categoryId 资源分类ID
     */
    private void checkCategoryExists(Long categoryId) {
        if (resourceCategoryMapper.selectById(categoryId) == null) {
            throw new ApiException("资源分类不存在");
        }
    }
}