package com.macro.mall.tiny.modules.ums.controller;

import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.modules.ums.dto.UmsRoleCreateRequest;
import com.macro.mall.tiny.modules.ums.model.UmsRole;
import com.macro.mall.tiny.modules.ums.service.UmsRoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


import org.springframework.web.bind.annotation.*;

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

}
