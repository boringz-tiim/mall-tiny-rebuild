package com.macro.mall.tiny.modules.ums.controller;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.ums.dto.UmsMenuNode;
import com.macro.mall.tiny.modules.ums.dto.UmsMenuRequest;
import com.macro.mall.tiny.modules.ums.model.UmsMenu;
import com.macro.mall.tiny.modules.ums.service.UmsMenuService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台菜单管理接口
 *
 * 负责接收菜单管理请求，校验参数，调用菜单Service并阻止统一响应
 */
@RestController
@RequestMapping("/menu")
public class UmsMenuController {
    private final UmsMenuService menuService;
    public UmsMenuController(UmsMenuService menuService){
        this.menuService=menuService;
    }
    /**
     * 查询指定父菜单下的直接子菜单
     */
    @GetMapping("/list/{parentId}")
    public CommonResult<List<UmsMenu>> list(
            @PathVariable
            @Min(value=0,message="父菜单ID不能小于0")
            Long parentId
    ){
        return CommonResult.success(menuService.listByParentId(parentId));
    }
    @GetMapping("/{id}")
    public CommonResult<UmsMenu> getDetail(
            @PathVariable
            @Min(value = 1,message = "菜单ID必须大于等于1")
            Long id
    ){
        return CommonResult.success(menuService.getDetail(id));
    }
    @PostMapping
    public CommonResult<UmsMenu> create(
            @Valid @RequestBody
            UmsMenuRequest request
    ){
        return CommonResult .success(
                menuService.create(request ),"菜单创建成功"
        ) ;
    }
    @PutMapping("/{id}")
    public CommonResult <UmsMenu> update(
            @PathVariable
            @Min(value = 1,message = "菜单ID必须大于等于1")
            Long id,
            @Valid @RequestBody
            UmsMenuRequest request
    ){
        return CommonResult .success(
                menuService.update(id,request),"菜单修改成功"
        ) ;
    }
    @DeleteMapping("/{id}")
    public CommonResult<Void> deleteMenu(
            @PathVariable
            @Min(value = 1, message = "菜单ID必须大于等于1")
            Long id
    ) {
        menuService.deleteMenu(id);

        return CommonResult.success(
                null,
                "菜单删除成功"
        );
    }
    /**
     * 查询完整菜单树。
     */
    @GetMapping("/tree")
    public CommonResult<List<UmsMenuNode>> treeList() {
        return CommonResult.success(
                menuService.treeList()
        );
    }
}
