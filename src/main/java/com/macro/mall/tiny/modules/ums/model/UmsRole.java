package com.macro.mall.tiny.modules.ums.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台角色实体，对应ums_role表
 */
@Data
@TableName("ums_role")
public class UmsRole {
    @TableId(value="id",type= IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    /**
     * 当前角色关联的后台用户数量
     *
     */
    private Integer adminCount;
    private LocalDateTime createTime;
    /**
     * 启用状态：0表示禁用，1表示启用
     *
     */
    private Integer status;
    /**
     * 排序值，数值越小通常越靠前
     */
    private Integer sort;
}
