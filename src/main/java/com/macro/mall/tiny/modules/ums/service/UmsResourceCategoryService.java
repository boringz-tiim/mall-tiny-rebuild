package com.macro.mall.tiny.modules.ums.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.macro.mall.tiny.modules.ums.dto.UmsResourceCategoryRequest;
import com.macro.mall.tiny.modules.ums.model.UmsResourceCategory;

import java.util.List;

/**
 * 后台接口资源分类业务接口。
 *
 * 定义资源分类的查询、新增、修改和删除业务。
 */
public interface UmsResourceCategoryService
        extends IService<UmsResourceCategory> {

    /**
     * 查询全部资源分类。
     *
     * @return 按排序值和ID升序排列的分类列表
     */
    List<UmsResourceCategory> listAll();

    /**
     * 根据ID查询资源分类详情。
     *
     * @param id 分类ID
     * @return 对应的资源分类
     */
    UmsResourceCategory getDetail(Long id);

    /**
     * 创建资源分类。
     *
     * @param request 分类创建参数
     * @return 创建后的资源分类
     */
    UmsResourceCategory create(
            UmsResourceCategoryRequest request
    );

    /**
     * 修改资源分类。
     *
     * @param id 分类ID
     * @param request 分类修改参数
     * @return 修改后的资源分类
     */
    UmsResourceCategory update(
            Long id,
            UmsResourceCategoryRequest request
    );

    /**
     * 删除资源分类。
     *
     * 分类下存在接口资源时禁止删除。
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);
}