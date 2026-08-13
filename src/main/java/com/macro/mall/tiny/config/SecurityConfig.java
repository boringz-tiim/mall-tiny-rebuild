package com.macro.mall.tiny.config;

import com.macro.mall.tiny.security.JwtAuthenticationFilter;
import com.macro.mall.tiny.security.RestAccessDeniedHandler;
import com.macro.mall.tiny.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration

public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    ) {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/admin","/admin/login","/admin/{id}/roles","/menu","/menu/{id}",
                                "/admin/logout","/admin/{id}","/admin/{id}/status","/admin/me/password","/role","/role/{id}","/role/{id}/status","/role/{id}/menus")
                )
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form->form.disable())
                .httpBasic(basic->basic.disable())
                .logout(logout -> logout.disable())
                .exceptionHandling(exceptions->exceptions.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/hello")
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/admin/me",
                                "/role/list",
                                "/role/{id}",
                                "/admin/{id}/roles",
                                "/menu/list/{parentId}",
                                "/menu/{id}",
                                "/menu/tree",
                                "/role/{id}/menus"
                        ).authenticated()
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/admin/{id}",
                                "/role/{id}",
                                "/admin/{id}/roles",
                                "/menu/{id}",
                                "/role/{id}/menus"
                        ).authenticated()
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/admin/{id}",
                                "/role/{id}",
                                "/menu/{id}"
                        ).authenticated()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/role",
                                "/menu"
                        )
                        .authenticated()
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/admin/{id}/status",
                                "/admin/me/password",
                                "/role/{id}/status"
                        )
                        .authenticated()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/admin/list",
                                "/admin/{id}"
                        )
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/admin/logout"
                        ).authenticated()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/admin",
                                "/admin/login"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                ).addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
