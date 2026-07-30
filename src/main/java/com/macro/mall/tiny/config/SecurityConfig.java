package com.macro.mall.tiny.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/admin")
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/hello")
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/admin/list",
                                "/admin/{id}"
                        )
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/admin"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                );

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
