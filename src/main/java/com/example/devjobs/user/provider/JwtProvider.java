package com.example.devjobs.user.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtProvider {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    
    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        // java-jwt는 키 길이 제한이 없음!
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.verifier = JWT.require(algorithm).build();
    }

    public String create(String loginId, String role, Long userId) {
        Date expiredDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));

        return JWT.create()
                .withSubject(loginId)
                .withClaim("role", role)
                .withClaim("userId", userId)
                .withIssuedAt(new Date())
                .withExpiresAt(expiredDate)
                .sign(algorithm);
    }

    public DecodedJWT validate(String jwt) {
        try {
            return verifier.verify(jwt);
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public String getLoginIdFromToken(String jwt) {
        DecodedJWT decodedJWT = validate(jwt);
        return decodedJWT != null ? decodedJWT.getSubject() : null;
    }
    
    public Long getUserIdFromToken(String jwt) {
        DecodedJWT decodedJWT = validate(jwt);
        return decodedJWT != null ? decodedJWT.getClaim("userId").asLong() : null;
    }
}