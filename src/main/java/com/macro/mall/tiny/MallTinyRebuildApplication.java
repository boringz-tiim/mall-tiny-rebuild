package com.macro.mall.tiny;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.macro.mall.tiny.modules.ums.mapper")
public class MallTinyRebuildApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallTinyRebuildApplication.class, args);
    }

}
