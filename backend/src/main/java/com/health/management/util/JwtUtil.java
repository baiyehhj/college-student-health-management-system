package com.health.management.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * 生成Token（学生用户）
     */
    public String generateToken(Long studentId, String studentNo) {
        return generateToken(studentId, studentNo, "STUDENT");
    }
    
    /**
     * 生成Token（带角色）
     */
    public String generateToken(Long userId, String userNo, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userNo", userNo);
        claims.put("role", role);
        // 兼容旧版本
        claims.put("studentId", userId);
        claims.put("studentNo", userNo);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userNo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) return null;
        Object userId = claims.get("userId");
        if (userId == null) {
            // 兼容旧版本
            userId = claims.get("studentId");
        }
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }
    
    /**
     * 从Token中获取学生ID（兼容旧版本）
     */
    public Long getStudentIdFromToken(String token) {
        return getUserIdFromToken(token);
    }
    
    /**
     * 从Token中获取用户编号
     */
    public String getUserNoFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) return null;
        Object userNo = claims.get("userNo");
        if (userNo == null) {
            // 兼容旧版本
            userNo = claims.get("studentNo");
        }
        return userNo != null ? userNo.toString() : claims.getSubject();
    }
    
    /**
     * 从Token中获取学号（兼容旧版本）
     */
    public String getStudentNoFromToken(String token) {
        return getUserNoFromToken(token);
    }
    
    /**
     * 从Token中获取角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) return null;
        Object role = claims.get("role");
        return role != null ? role.toString() : "STUDENT";
    }
    
    /**
     * 验证Token是否有效
     */
    public Boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 判断Token是否过期
     */
    private Boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
