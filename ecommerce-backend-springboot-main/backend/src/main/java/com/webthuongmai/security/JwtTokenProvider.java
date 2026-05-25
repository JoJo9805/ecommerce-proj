package com.webthuongmai.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Must be at least 256-bits (32 characters)
    private static final String JWT_SECRET = "ECommerceBackendSpringBootSecureSecretKey2026PerfectSecretKey";
    private static final Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    
    // 24 hours expiration
    private static final long JWT_EXPIRATION = 86400000L;

    public String generateToken(Authentication authentication) {
        org.springframework.security.core.userdetails.User userPrincipal = 
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Token validation failed
        }
        return false;
    }
}
