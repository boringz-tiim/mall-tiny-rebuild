package com.macro.mall.tiny.modules.ums.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.spring.service.IService;
import com.macro.mall.tiny.modules.ums.dto.UmsAdminCreateRequest;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;


import java.util.List;

public interface UmsAdminService extends IService<UmsAdmin> {
   // List<UmsAdmin> listAll();
   Page<UmsAdmin> list(String keyword, long pageSize, long pageNum);
    UmsAdmin getDetail(Long id);
    //创建用户
    UmsAdmin create(UmsAdminCreateRequest umsAdminCreateRequest);
}
