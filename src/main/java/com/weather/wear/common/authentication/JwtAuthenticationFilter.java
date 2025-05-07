package com.weather.wear.common.authentication;

import com.weather.wear.common.exception.BaseException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtProvider; // JWT 검증 로직을 처리하는 클래스
    private final TokenService tokenService; // AccessToken과 RefreshToken 검증을 처리하는 서비스

    private static final List<String> EXCLUDE_URLS = List.of(
            "/user/register", "/user/login", "/"
    );

    public JwtAuthenticationFilter(JwtTokenProvider jwtProvider, TokenService tokenService) {
        this.jwtProvider = jwtProvider;
        this.tokenService = tokenService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (EXCLUDE_URLS.contains(path)) {
            filterChain.doFilter(request, response); // 그냥 통과시킴
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String userEmail = null;

        try {
            // Authorization 헤더에서 Bearer 토큰 추출
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7); // "Bearer " 부분을 제거하고 토큰만 추출
                userEmail = jwtProvider.getUserEmail(token, true); // 토큰에서 사용자 이메일 추출
            }
            // 토큰이 존재하고 SecurityContext에 인증이 없는 경우
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // JWT 토큰이 유효한지 검증
                if (jwtProvider.validateToken(token, true)) {
                    // JWT가 유효하면, 사용자 인증을 설정
                    var authentication = jwtProvider.getAuthentication(token);
                    // 인증된 권한 목록을 설정
                    if (authentication.getAuthorities() == null || authentication.getAuthorities().isEmpty()) {
                        // 필요한 경우, 기본 권한 설정
                        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                        authentication = new UsernamePasswordAuthenticationToken(
                                authentication.getPrincipal(),
                                authentication.getCredentials(),
                                authorities
                        );
                    }
                    // SecurityContext에 사용자 인증을 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);

        } catch (BaseException e) {
            // 에러 응답 직접 작성
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");

            String json = String.format("""
                    {
                        "success": false,
                        "status": "UNAUTHORIZED",
                        "message": "%s"
                    }
                    """, e.getMessage());

            response.getWriter().write(json);
        }
    }
}
