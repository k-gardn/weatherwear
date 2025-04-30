package com.weather.wear.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.wear.common.exception.BaseException;
import com.weather.wear.common.JwtTokenProvider;
import com.weather.wear.common.TokenService;
import com.weather.wear.common.response.ErrorResponseStatus;
import com.weather.wear.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class JwtInterceptor implements HandlerInterceptor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TokenService tokenService; // 토큰 검증 서비스

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // 토큰 검증 서비스

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");
        log.debug("authorizationHeader : {}", authorizationHeader);
        String refreshTokenHeader = request.getHeader("Refresh-Token"); // Refresh Token이 포함된 헤더

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring(7); // Bearer 제거
            log.debug("accessToken : {}", accessToken);
            // AccessToken 검증
            try {
                if (tokenService.validateAccessToken(accessToken)) {
                    String userEmail = jwtTokenProvider.getUserEmail(accessToken, true);
                    request.setAttribute("userEmail", userEmail);
                    return true; // AccessToken 유효성 검증 성공
                }
            } catch (BaseException e) {
                // 만료된 AccessToken이라면, RefreshToken을 통해 새 AccessToken 발급 시도
                if (refreshTokenHeader != null) {
                    String refreshToken = refreshTokenHeader;

                    // RefreshToken에서 사용자 이메일 추출
                    String userEmail = jwtTokenProvider.getUserEmail(refreshToken, false); // RefreshToken에서 이메일 추출

                    // RefreshToken 검증
                    if (tokenService.validateRefreshToken(refreshToken, userEmail)) {
                        // 새로운 AccessToken 발급
                        String newAccessToken = jwtTokenProvider.generateAccessToken(userEmail);
                        response.setHeader("Authorization", "Bearer " + newAccessToken);

                        // 사용자 이메일을 다시 설정
                        request.setAttribute("userEmail", userEmail);

                        return true; // 새로 발급한 AccessToken으로 인증 통과
                    }
                }
            }
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        return false; // 인증 실패
    }
}

