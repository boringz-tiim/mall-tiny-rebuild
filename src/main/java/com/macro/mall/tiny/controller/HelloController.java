package com.macro.mall.tiny.controller;

import com.macro.mall.tiny.common.api.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HelloController {
    @GetMapping("/hello")
    public CommonResult<String> hello(){
        return CommonResult.success("hello,mall-tiny");
    }
}
