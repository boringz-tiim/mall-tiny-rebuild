package com.macro.mall.tiny.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.tiny.modules.ums.model.UmsResourceCategory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 后台接口资源分类数据库访问接口
 *
 * 继承MyBatis-Plus BaseMapper ，提供资源分类基础CRUD能力
 */
public interface UmsResourceCategoryMapper extends BaseMapper<UmsResourceCategory> {
/**
 * 统计指定分类下的接口资源数量
 */
@Select("""
select count(*) from ums_resource
where category_id = #{categoryId}
""")
long countResourcesByCategoryId(@Param("categoryId") Long categoryId);
}
