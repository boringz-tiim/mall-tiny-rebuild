package com.macro.mall.tiny.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties (
    @NotBlank(message="JWT密钥不能为空")
    String secret,
    @NotNull(message = "JWT过期时间不能为空")
    Duration expiration
){

}
