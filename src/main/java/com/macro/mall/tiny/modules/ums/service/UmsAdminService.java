package com.macro.mall.tiny.modules.ums.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.spring.service.IService;
import com.macro.mall.tiny.modules.ums.dto.UmsAdminCreateRequest;
import com.macro.mall.tiny.modules.ums.dto.UmsAdminLoginRequest;
import com.macro.mall.tiny.modules.ums.dto.UmsAdminPasswordRequest;
import com.macro.mall.tiny.modules.ums.dto.UmsAdminUpdateRequest;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;




public interface UmsAdminService extends IService<UmsAdmin> {
   // List<UmsAdmin> listAll();
   Page<UmsAdmin> list(String keyword, long pageSize, long pageNum);
    UmsAdmin getDetail(Long id);
    //创建用户
    UmsAdmin create(UmsAdminCreateRequest umsAdminCreateRequest);
    UmsAdmin getByUsername(String username);

    /**
     * 负责验证身份
     * @param request
     * @return
     */
    UmsAdmin authenticate(UmsAdminLoginRequest request);
    /**
     * 负责验证成功后生成token
     */
    String login(UmsAdminLoginRequest request);
    /**
     * 修改指定后台用户的基本信息
     * 只允许修改头像、邮箱、昵称和备注
     * 不负责修改用户名、密码和账号状态
     */
    UmsAdmin updateBasicInfo(Long id, UmsAdminUpdateRequest request);
 /**
  * 修改后台用户状态
  * @param id 用户ID
  * @param status 0 表示禁用，1表示启用
  * @return 修改后的用户
  */
   UmsAdmin updateStatus(Long id,Integer status);
/**
 * 修改当前登录用户的密码
 *
 */
    void changePassword(Long adminId, UmsAdminPasswordRequest request);
}
