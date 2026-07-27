package com.macro.mall.tiny.common.api;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public record CommonPage<T>(long pageNum, long pageSize,
                            long totalPage, long total, List<T> list) {
    public static <T> CommonPage<T> from(IPage<T> page){
        return new CommonPage<>(
                page.getCurrent(),
                page.getSize(),
                page.getPages(),
                page.getTotal(),
               // page.getTotal(),
                page.getRecords()
        );
    }
}
