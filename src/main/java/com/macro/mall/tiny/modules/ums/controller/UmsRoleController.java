package com.macro.mall.tiny.modules.ums.controller;

import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.modules.ums.dto.UmsRoleCreateRequest;
import com.macro.mall.tiny.modules.ums.dto.UmsRoleMenuRequest;
import com.macro.mall.tiny.modules.ums.dto.UmsRoleStatusRequest;
import com.macro.mall.tiny.modules.ums.dto.UmsRoleUpdateRequest;
import com.macro.mall.tiny.modules.ums.model.UmsMenu;
import com.macro.mall.tiny.modules.ums.model.UmsRole;
import com.macro.mall.tiny.modules.ums.service.UmsRoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台角色管理接口
 */
@RestController
@RequestMapping("/role")
public class UmsRoleController {
    private final UmsRoleService roleService;


    public UmsRoleController (UmsRoleService roleService){
       this.roleService=roleService;
    }
   @GetMapping("/list")
    public CommonResult<CommonPage<UmsRole>> list(
            @RequestParam(required =false)
            String keyword,
            @RequestParam (defaultValue = "5")
            @Min(value=1,message = "pageSize必须大于等于1")
            @Max(value=100,message="pageSize不能大于100")
            long pageSize,
            @RequestParam(defaultValue = "1")
            @Min(value = 1,message = "pageNum必须大于等于1")
            long pageNum

   ){
        Page<UmsRole> rolePage = roleService.list(keyword,pageSize,pageNum);
        return CommonResult.success(CommonPage.from(rolePage));
   }
   @PostMapping
    public CommonResult<UmsRole> create(
            @Valid @RequestBody
            UmsRoleCreateRequest request
   ){
        UmsRole role = roleService.create(request);
        return CommonResult.success(
                role,
                "新角色创建成功"
        );
   }
   @GetMapping("/{id}")
    public CommonResult<UmsRole>getDetail(
            @PathVariable
            @Min(value = 1,message = "角色id必须大于等于1")
            Long id
   ){
        return CommonResult.success(
                roleService.getDetail(id)
        );
   }
    @PutMapping("/{id}")
    public CommonResult<UmsRole> update(
            @PathVariable
            @Min(value = 1, message = "角色Id必须大于等于1")
            Long id,

            @Valid @RequestBody
            UmsRoleUpdateRequest request
    ) {
        UmsRole role = roleService.update(id, request);

        return CommonResult.success(
                role,
                "角色修改成功"
        );
    }
    @PatchMapping("/{id}/status")
    public CommonResult<UmsRole> updateStatus(
            @PathVariable
            @Min(value = 1, message = "角色ID必须大于等于1")
            Long id,

            @Valid @RequestBody
            UmsRoleStatusRequest request
    ){
        UmsRole role = roleService.updateStatus(
                id,request.status());
        return CommonResult.success(role,"角色状态修改成功");
    }
    @DeleteMapping("/{id}")
    public CommonResult<Void> deleteRole(
            @PathVariable
            @Min(value = 1,message = "角色ID必须大于等于1")
            Long id
    ){
        roleService.deleteRole(id);
        return CommonResult.success(null,"角色删除成功");
    }
    /**
     * 查询指定角色拥有的菜单。
     */
    @GetMapping("/{id}/menus")
    public CommonResult<List<UmsMenu>> getMenuList(
            @PathVariable
            @Min(value = 1, message = "角色ID必须大于等于1")
            Long id
    ) {
        return CommonResult.success(
                roleService.getMenuList(id)
        );
    }
    /**
     * 重新分配指定角色的菜单。
     */
    @PutMapping("/{id}/menus")
    public CommonResult<List<UmsMenu>> updateMenus(
            @PathVariable
            @Min(value = 1, message = "角色ID必须大于等于1")
            Long id,

            @Valid @RequestBody
            UmsRoleMenuRequest request
    ) {
        List<UmsMenu> menus = roleService.updateMenus(
                id,
                request.menuIds()
        );

        return CommonResult.success(
                menus,
                "角色菜单分配成功"
        );
    }
}
