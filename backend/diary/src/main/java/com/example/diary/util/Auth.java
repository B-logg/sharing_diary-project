package com.example.diary.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Auth {
    public static Long currentUserId() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || a.getPrincipal() == null) return null;
        if (a.getPrincipal() instanceof Long id) return id;
        // JwtAuthenticationFilter에서 principal을 Long(userId)로 세팅했다는 전제
        return null;
    }
}

