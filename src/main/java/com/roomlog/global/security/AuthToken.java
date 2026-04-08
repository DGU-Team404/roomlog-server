package com.roomlog.global.security;

import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class AuthToken {

    private final SecretKey key;
    private final long expiration;
    private final long refreshExpiration;

    public AuthToken(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String generateToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "access")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpiration))
                .signWith(key)
                .compact();
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.AUTH_006);
        }
    }

    public Long getUserIdFromRefreshToken(String token) {
        Claims claims = parseClaims(token);
        String type = claims.get("type", String.class);
        if (!"refresh".equals(type)) {
            throw new CustomException(ErrorCode.AUTH_006);
        }
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.AUTH_006);
        }
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.AUTH_006);
        }
    }
}
