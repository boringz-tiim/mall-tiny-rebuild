package com.macro.mall.tiny.modules.ums.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台接口资源实体。
 *
 * 对应 ums_resource 表，描述需要进行权限控制的后端接口资源。
 */
@Data
@TableName("ums_resource")
public class UmsResource {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资源创建时间。
     */
    private LocalDateTime createTime;

    /**
     * 资源名称。
     */
    private String name;

    /**
     * 需要进行权限控制的URL模式。
     */
    private String url;

    /**
     * 资源说明。
     */
    private String description;

    /**
     * 所属资源分类ID。
     */
    private Long categoryId;
}