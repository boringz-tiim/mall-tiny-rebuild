package com.macro.mall.tiny.modules.ums.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.macro.mall.tiny.modules.ums.model.UmsRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;



public interface UmsRoleService extends IService <UmsRole>{
/**
 * 根据角色名称分页查询角色
 */
Page<UmsRole> list(String keyword,long pageSize,long pageNum);
}
