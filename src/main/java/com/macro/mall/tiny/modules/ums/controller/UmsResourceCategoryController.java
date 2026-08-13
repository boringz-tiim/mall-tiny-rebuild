package com.macro.mall.tiny.modules.ums.controller;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.ums.dto.UmsResourceCategoryRequest;
import com.macro.mall.tiny.modules.ums.model.UmsResourceCategory;
import com.macro.mall.tiny.modules.ums.service.UmsResourceCategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台接口资源分类管理接口。
 *
 * 负责接收资源分类管理请求、校验参数、调用 Service，
 * 并将业务结果包装为统一响应。
 */
@RestController
@RequestMapping("/resource-category")
public class UmsResourceCategoryController {

    private final UmsResourceCategoryService resourceCategoryService;

    public UmsResourceCategoryController(
            UmsResourceCategoryService resourceCategoryService
    ) {
        this.resourceCategoryService = resourceCategoryService;
    }

    /**
     * 查询全部资源分类。
     *
     * @return 按排序值和ID排列的资源分类列表
     */
    @GetMapping("/list")
    public CommonResult<List<UmsResourceCategory>> listAll() {
        return CommonResult.success(
                resourceCategoryService.listAll()
        );
    }

    /**
     * 查询资源分类详情。
     *
     * @param id 分类ID
     * @return 对应的资源分类
     */
    @GetMapping("/{id}")
    public CommonResult<UmsResourceCategory> getDetail(
            @PathVariable
            @Min(value = 1, message = "资源分类ID必须大于等于1")
            Long id
    ) {
        return CommonResult.success(
                resourceCategoryService.getDetail(id)
        );
    }

    /**
     * 创建资源分类。
     *
     * @param request 分类创建参数
     * @return 创建后的资源分类
     */
    @PostMapping
    public CommonResult<UmsResourceCategory> create(
            @Valid @RequestBody
            UmsResourceCategoryRequest request
    ) {
        return CommonResult.success(
                resourceCategoryService.create(request),
                "资源分类创建成功"
        );
    }

    /**
     * 修改资源分类。
     *
     * @param id 分类ID
     * @param request 分类修改参数
     * @return 修改后的资源分类
     */
    @PutMapping("/{id}")
    public CommonResult<UmsResourceCategory> update(
            @PathVariable
            @Min(value = 1, message = "资源分类ID必须大于等于1")
            Long id,
            @Valid @RequestBody
            UmsResourceCategoryRequest request
    ) {
        return CommonResult.success(
                resourceCategoryService.update(id, request),
                "资源分类修改成功"
        );
    }

    /**
     * 删除资源分类。
     *
     * @param id 分类ID
     * @return 删除成功响应
     */
    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(
            @PathVariable
            @Min(value = 1, message = "资源分类ID必须大于等于1")
            Long id
    ) {
        resourceCategoryService.deleteCategory(id);

        return CommonResult.success(
                null,
                "资源分类删除成功"
        );
    }
}