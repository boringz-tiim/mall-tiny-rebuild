package com.macro.mall.tiny.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import java.util.List;

@Component

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX="Bearer ";
    private final JwtTokenService jwtTokenService;
    public JwtAuthenticationFilter(JwtTokenService jwtTokenService){
        this.jwtTokenService=jwtTokenService;
    }
    @Override //继承父类OnceperRequestFilter里面已经定义了doFilterInternal(),现在重新实习那，叫做方法重写
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization=request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorization==null || !authorization.startsWith(BEARER_PREFIX)){
            filterChain.doFilter(request,response);
            return ;
        }
        String token=authorization.substring(BEARER_PREFIX.length()).trim();
        try {
            authenticateRequest(request,token);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            request.setAttribute("jwtException",exception);
        }
        filterChain.doFilter(request,response);

    }
    private void authenticateRequest(HttpServletRequest request, String token){
        if(SecurityContextHolder.getContext().getAuthentication()!=null){
            return ;
        }
        Claims claims =
                jwtTokenService.parseToken(token);

        String username =
                jwtTokenService.getUsername(claims);

        Long adminId =
                jwtTokenService.getAdminId(claims);

        if (username == null || username.isBlank()) {
            throw new JwtException(
                    "Token中缺少合法的用户名"
            );
        }
        JwtAdminPrincipal principal = new JwtAdminPrincipal(adminId,username);
        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
                principal,null, List.of()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }
}
