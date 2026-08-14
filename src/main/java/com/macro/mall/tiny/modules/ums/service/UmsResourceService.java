package com.macro.mall.tiny.modules.ums.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.macro.mall.tiny.modules.ums.dto.UmsResourceRequest;
import com.macro.mall.tiny.modules.ums.model.UmsResource;

/**
 * 后台接口资源业务接口。
 *
 * 定义接口资源的分页查询、详情、新增、修改和删除业务。
 */
public interface UmsResourceService
        extends IService<UmsResource> {

    /**
     * 根据分类、资源名称和URL分页查询接口资源。
     *
     * @param categoryId 资源分类ID，为null时不限制分类
     * @param nameKeyword 资源名称关键字
     * @param urlKeyword 资源URL关键字
     * @param pageSize 每页数量
     * @param pageNum 页码
     * @return 接口资源分页结果
     */
    Page<UmsResource> list(
            Long categoryId,
            String nameKeyword,
            String urlKeyword,
            long pageSize,
            long pageNum
    );

    /**
     * 根据ID查询接口资源详情。
     *
     * @param id 资源ID
     * @return 对应的接口资源
     */
    UmsResource getDetail(Long id);

    /**
     * 创建接口资源。
     *
     * @param request 资源创建参数
     * @return 创建后的资源
     */
    UmsResource create(UmsResourceRequest request);

    /**
     * 修改接口资源。
     *
     * @param id 资源ID
     * @param request 资源修改参数
     * @return 修改后的资源
     */
    UmsResource update(
            Long id,
            UmsResourceRequest request
    );

    /**
     * 删除接口资源及其角色关联关系。
     *
     * @param id 资源ID
     */
    void deleteResource(Long id);
}