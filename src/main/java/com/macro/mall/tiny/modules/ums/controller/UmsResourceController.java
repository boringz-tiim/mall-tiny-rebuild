package com.macro.mall.tiny.modules.ums.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.ums.dto.UmsResourceRequest;
import com.macro.mall.tiny.modules.ums.model.UmsResource;
import com.macro.mall.tiny.modules.ums.service.UmsResourceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

/**
 * 后台接口资源管理接口。
 *
 * 负责接收接口资源管理请求、校验参数、调用 Service，
 * 并将业务结果包装为统一响应。
 */
@RestController
@RequestMapping("/resource")
public class UmsResourceController {

    private final UmsResourceService resourceService;

    public UmsResourceController(
            UmsResourceService resourceService
    ) {
        this.resourceService = resourceService;
    }

    /**
     * 分页查询接口资源。
     *
     * @param categoryId 资源分类ID
     * @param nameKeyword 资源名称关键字
     * @param urlKeyword 资源URL关键字
     * @param pageSize 每页数量
     * @param pageNum 页码
     * @return 接口资源分页结果
     */
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsResource>> list(
            @RequestParam(required = false)
            @Min(value = 1, message = "资源分类ID必须大于等于1")
            Long categoryId,

            @RequestParam(required = false)
            String nameKeyword,

            @RequestParam(required = false)
            String urlKeyword,

            @RequestParam(defaultValue = "5")
            @Min(value = 1, message = "pageSize必须大于等于1")
            @Max(value = 100, message = "pageSize不能大于100")
            long pageSize,

            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "pageNum必须大于等于1")
            long pageNum
    ) {
        Page<UmsResource> resourcePage =
                resourceService.list(
                        categoryId,
                        nameKeyword,
                        urlKeyword,
                        pageSize,
                        pageNum
                );

        return CommonResult.success(
                CommonPage.from(resourcePage)
        );
    }

    /**
     * 查询接口资源详情。
     *
     * @param id 资源ID
     * @return 对应的接口资源
     */
    @GetMapping("/{id}")
    public CommonResult<UmsResource> getDetail(
            @PathVariable
            @Min(value = 1, message = "资源ID必须大于等于1")
            Long id
    ) {
        return CommonResult.success(
                resourceService.getDetail(id)
        );
    }

    /**
     * 创建接口资源。
     *
     * @param request 资源创建参数
     * @return 创建后的资源
     */
    @PostMapping
    public CommonResult<UmsResource> create(
            @Valid @RequestBody
            UmsResourceRequest request
    ) {
        return CommonResult.success(
                resourceService.create(request),
                "接口资源创建成功"
        );
    }

    /**
     * 修改接口资源。
     *
     * @param id 资源ID
     * @param request 资源修改参数
     * @return 修改后的资源
     */
    @PutMapping("/{id}")
    public CommonResult<UmsResource> update(
            @PathVariable
            @Min(value = 1, message = "资源ID必须大于等于1")
            Long id,

            @Valid @RequestBody
            UmsResourceRequest request
    ) {
        return CommonResult.success(
                resourceService.update(id, request),
                "接口资源修改成功"
        );
    }

    /**
     * 删除接口资源。
     *
     * 同时清理该资源对应的角色资源关系。
     *
     * @param id 资源ID
     * @return 删除成功响应
     */
    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(
            @PathVariable
            @Min(value = 1, message = "资源ID必须大于等于1")
            Long id
    ) {
        resourceService.deleteResource(id);

        return CommonResult.success(
                null,
                "接口资源删除成功"
        );
    }
}