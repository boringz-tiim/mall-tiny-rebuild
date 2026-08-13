package com.macro.mall.tiny.modules.ums.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台接口资源分类实体
 *
 * 对应ums_resource_category表，用于对接口资源进行分组管理
 */
@Data
@TableName("ums_resource_category")
public class UmsResourceCategory {
    @TableId(value = "id",type = IdType.AUTO )
    private Long id;
    private LocalDateTime createTime;
    private String name;
    private Integer sort;
}
