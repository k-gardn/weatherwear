package com.weather.wear.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwt = request.getHeader("Authorization");

        if (jwt != null && jwt.startsWith("Bearer ")) {
            // JWT 검증 로직
            jwt = jwt.substring(7); // "Bearer " 제거
            // 토큰 검증 (예: Jwts.parser().setSigningKey(...).parseClaimsJws(jwt);)
            // 유효성 검사 및 사용자 인증
            return true; // 인증 성공
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        return false; // 인증 실패
    }
}

