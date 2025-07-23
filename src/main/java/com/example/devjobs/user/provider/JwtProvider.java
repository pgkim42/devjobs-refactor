package com.example.devjobs.user.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
    
    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        // If the provided key is too short, generate a secure one
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) { // 256 bits = 32 bytes
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            this.key = Keys.hmacShaKeyFor(keyBytes);
        }
    }

    public String create(String loginId, String role, Long userId) {
        Date expiredDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));

        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(loginId)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .compact();
    }

    public Claims validate(String jwt) {

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public String getLoginIdFromToken(String jwt) {
        Claims claims = validate(jwt);
        return claims != null ? claims.getSubject() : null;
    }
    
    public Long getUserIdFromToken(String jwt) {
        Claims claims = validate(jwt);
        return claims != null ? claims.get("userId", Long.class) : null;
    }
}
