package com.weather.wear.common;

import com.weather.wear.common.exception.BaseException;
import com.weather.wear.common.response.ErrorResponseStatus;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;

@RequiredArgsConstructor
@Component
@PropertySource("classpath:application.yml")
public class JwtTokenProvider {

    @Value("${jwt.accessExpTime}")
    private long accessExpTime;

    @Value("${jwt.refExpTime}")
    private long refExpTime;

    @Value("${jwt.secretKey}")
    private String secretKey; // YAML에서 비밀 키 읽기
    // SecretKey를 분리하여 선언
//    private final SecretKey accessTokenSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
//    private final SecretKey refreshTokenSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private SecretKey accessTokenSecretKey;
    private SecretKey refreshTokenSecretKey;

    @PostConstruct
    public void init() {
        accessTokenSecretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        refreshTokenSecretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // JWT 토큰 생성 메서드
    public String generateAccessToken(String userEmail) {
//        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpTime))
                .signWith(accessTokenSecretKey)
                .compact();
    }

    // JWT 토큰 생성 메서드
    public String generateRefreshToken(String userEmail) {
//        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refExpTime))
                .signWith(refreshTokenSecretKey)
                .compact();
    }

    //
    public boolean validateToken(String  token, boolean isAccessToken) {
        try {
            // 서명 검증을 위한 SecretKey 설정
            SecretKey secretKey = isAccessToken ? accessTokenSecretKey : refreshTokenSecretKey;

            // 토큰 파싱
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build() // parserBuilder()에는 반드시 .build() 호출이 필요
                    .parseClaimsJws(token);

            // 만료 시간 확인 - 만료된 경우 ExpiredJwtException이 자동으로 발생
            // 만료 시간 확인
            return claims.getBody().getExpiration().after(new Date());
        } catch (ExpiredJwtException ignored) {
            // 만료된 토큰 예외 처리
            throw new BaseException(ErrorResponseStatus.EXPIRED_JWT);
//            return false;
        } catch (Exception e) {
            // 올바르지 않은 토큰일 경우 예외 발생
            throw new BaseException(ErrorResponseStatus.INVALID_JWT);
        }
    }

    // 토큰에서 이메일 추출
    public String getUserEmail(String token, boolean isAccessToken) {
        SecretKey secretKey = isAccessToken ? accessTokenSecretKey : refreshTokenSecretKey;

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject(); // 토큰의 subject가 사용자 이메일임
        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature for token: " + token);
            throw new RuntimeException("Invalid JWT signature");
        } catch (Exception e) {
            // 다른 예외 처리
            System.out.println("JWT parsing error: " + e.getMessage());
            throw new RuntimeException("JWT parsing error");
        }
    }

    // JWT를 기반으로 인증 정보를 생성하는 메서드
    public Authentication getAuthentication(String accessToken) {
        String userEmail = getUserEmail(accessToken, true);
        // 이 부분에서 사용자의 역할이나 권한을 추가할 수 있음
        return new UsernamePasswordAuthenticationToken(userEmail, null, new ArrayList<>());
    }
}
