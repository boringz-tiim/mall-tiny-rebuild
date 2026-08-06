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
                        .ignoringRequestMatchers("/admin","/admin/login","/admin/logout")
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
                                "/admin/me"
                        ).authenticated()
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
