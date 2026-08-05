package com.macro.mall.tiny.modules.ums.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.ums.dto.*;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;
import com.macro.mall.tiny.modules.ums.service.UmsAdminService;
import com.macro.mall.tiny.security.JwtAdminPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/admin")
public class UmsAdminController {
    //构造器依赖注入
    private final UmsAdminService adminService;
   //构造器名称与类名UmsAdminController完全相同，没有返回类型，创建Controller对象时自动执行
    public UmsAdminController(UmsAdminService adminService){
        this.adminService=adminService;
        //左边this.adminService表示当前Controller对象 的成员变量
        //右侧adminService 表示构造器参数
        //将Spring传入的Service对象保存到Controller的成员变量中
    }
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsAdminSummary>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "5")
            @Min(value = 1,message = "pageSize必须大于等于1")
            @Max(value = 100,message = "pageSize不能大于100") long pageSize,
            @RequestParam(defaultValue = "1")
            @Min(value=1,message = "pageNum必须大于等于1") long pageNum
    ) {
        Page<UmsAdmin> adminPage =
                adminService.list(keyword, pageSize, pageNum);

        Page<UmsAdminSummary> summaryPage =
                (Page<UmsAdminSummary>) adminPage.convert(UmsAdminSummary::from);

        return CommonResult.success(CommonPage.from(summaryPage));
    }
    @GetMapping("/{id}")
    public CommonResult<UmsAdminSummary>getDetail(@PathVariable
                                                  @Min(value = 1,message="用户Id必须大于等于1")Long id){
        UmsAdmin admin = adminService.getDetail(id);
        UmsAdminSummary summary = UmsAdminSummary.from(admin);
        return CommonResult.success(summary);
    }
    @PostMapping
    public CommonResult<UmsAdminSummary> create(@Valid @RequestBody UmsAdminCreateRequest request){

        UmsAdmin admin = adminService.create(request);
        return CommonResult.success(UmsAdminSummary.from(admin),"用户创建成功");
    }
    /**
     * 登录接口
     */
    @PostMapping("/login")
    public CommonResult<UmsAdminLoginResponse> loginResponseCommonResult(
            @Valid @RequestBody
            UmsAdminLoginRequest request
    ){
        String token=adminService.login(request);
        UmsAdminLoginResponse response = new UmsAdminLoginResponse(
                token,"Bearer"
        );
        return CommonResult.success(response,"登录成功");
    }

    @GetMapping("/me")
    public CommonResult<UmsAdminCurrentResponse>me(@AuthenticationPrincipal JwtAdminPrincipal principal){
        UmsAdminCurrentResponse response = new UmsAdminCurrentResponse(
                principal.adminId(),
                principal.username()
        );
        return CommonResult.success(response);
    }

}
