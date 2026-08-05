package com.macro.mall.tiny.security;

import com.macro.mall.tiny.config.JwtProperties;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service

public class JwtTokenService {
    private final SecretKey signingKey;
    private final Duration expiration;
    private final JwtParser jwtParser;
    public JwtTokenService(JwtProperties properties) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(properties.secret());
            this.signingKey= Keys.hmacShaKeyFor(keyBytes);

        } catch (DecodingException | WeakKeyException exception) {
            throw new IllegalStateException("JWT密钥必须是有效的Base64,并且解码后至少为32字节",exception);
        }
        if(properties.expiration().isZero()||properties.expiration().isNegative()){
            throw new IllegalStateException("JWT过期时间必须大于0");

        }
        this.expiration=properties.expiration();
        this.jwtParser=Jwts.parser()
                .verifyWith(signingKey)
                .build();
    }
    public String generateToken(UmsAdmin admin){
        Instant issueAt=Instant.now();
        Instant expiresAt=issueAt.plus(expiration);
        return Jwts.builder()
                .subject(admin.getUsername())
                .claim("adminId",admin.getId())
                .issuedAt(Date.from(issueAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey,Jwts.SIG.HS256)
                .compact();

    }
    public Claims parseToken(String token){
        if(token==null || token.isBlank()){
            throw new IllegalArgumentException("Token 不能为空");
        }
        return jwtParser.
                parseSignedClaims(token)
                .getPayload();
    }
    public String getUsername(Claims claims){
        return claims.getSubject();
    }
    public Long getAdminId(Claims claims){
        Object value = claims.get("adminId");
        if(!(value instanceof Number number)){
            throw new JwtException("token中缺少合法的adminId");
        }
        return number.longValue();
    }

}
