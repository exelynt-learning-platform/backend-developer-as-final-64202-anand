package com.example.booking.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.secret:}")
    private String secret;

    @Value("${jwt.expiration-ms:3600000}")
    private long validityInMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT_SECRET environment variable or jwt.secret property must be set and cannot be empty.");
        }
        if (secret.getBytes().length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 256 bits (32 bytes) long for HS256 algorithm.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private Key getSigningKey() {
        return signingKey;
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
