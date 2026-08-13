package com.macro.mall.tiny.modules.ums.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.macro.mall.tiny.common.exception.ApiException;
import com.macro.mall.tiny.modules.ums.dto.UmsResourceCategoryRequest;
import com.macro.mall.tiny.modules.ums.mapper.UmsResourceCategoryMapper;
import com.macro.mall.tiny.modules.ums.model.UmsResourceCategory;
import com.macro.mall.tiny.modules.ums.service.UmsResourceCategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台接口资源分类业务实现类。
 *
 * 负责资源分类的排序查询、详情校验和 CRUD 业务处理。
 */
@Service
public class UmsResourceCategoryServiceImpl
        extends ServiceImpl<
                UmsResourceCategoryMapper,
                UmsResourceCategory
                >
        implements UmsResourceCategoryService {

    @Override
    public List<UmsResourceCategory> listAll() {
        return list(
                Wrappers.<UmsResourceCategory>lambdaQuery()
                        .orderByAsc(
                                UmsResourceCategory::getSort,
                                UmsResourceCategory::getId
                        )
        );
    }

    @Override
    public UmsResourceCategory getDetail(Long id) {
        UmsResourceCategory category = getById(id);

        if (category == null) {
            throw new ApiException("资源分类不存在");
        }

        return category;
    }

    @Override
    public UmsResourceCategory create(
            UmsResourceCategoryRequest request
    ) {
        UmsResourceCategory category =
                new UmsResourceCategory();

        category.setName(request.name());
        category.setSort(request.sort());
        category.setCreateTime(LocalDateTime.now());

        boolean saved = save(category);

        if (!saved) {
            throw new ApiException("创建资源分类失败");
        }

        return category;
    }

    @Override
    public UmsResourceCategory update(
            Long id,
            UmsResourceCategoryRequest request
    ) {
        UmsResourceCategory category = getDetail(id);

        category.setName(request.name());
        category.setSort(request.sort());

        boolean updated = updateById(category);

        if (!updated) {
            throw new ApiException("修改资源分类失败");
        }

        return category;
    }

    @Override
    public void deleteCategory(Long id) {
        getDetail(id);

        long resourceCount =
                baseMapper.countResourcesByCategoryId(id);

        if (resourceCount > 0) {
            throw new ApiException(
                    "当前分类下存在接口资源，不能删除"
            );
        }

        boolean removed = removeById(id);

        if (!removed) {
            throw new ApiException("删除资源分类失败");
        }
    }
}