package com.example.diary.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private final Key key;
    private final long expiryMs;

    public JwtUtil(String secret, long expiryMs) {
        // HS256은 32바이트 이상 권장
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiryMs = expiryMs;
    }

    /** userId, username으로 서명된 JWT 생성 */
    public String createToken(Long userId, String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiryMs);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("uname", username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 유효성 검사 + Claims 반환 (검증 실패 시 예외 던짐) */
    public Claims parseClaims(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }

    /** 토큰에서 userId(Long) 추출 */
    public Long getUserId(String token) throws JwtException {
        String sub = parseClaims(token).getSubject();
        return Long.valueOf(sub);
    }
}

