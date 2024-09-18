package com.weather.wear.common;

import com.weather.wear.member.domain.Member;
import com.weather.wear.member.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtProvider; // JWT 생성 및 검증 로직을 포함한 클래스

    // AccessToken 검증 메서드
    public boolean validateAccessToken(String accessToken) {
        return jwtProvider.validateToken(accessToken, true); // true -> AccessToken 검증
    }

    // RefreshToken 검증 메서드
    public boolean validateRefreshToken(String refreshToken) {
        return jwtProvider.validateToken(refreshToken, false); // false -> RefreshToken 검증
    }

    // DB에 저장된 refreshToken과 비교
    public boolean validateRefreshToken(String refreshToken, String userEmail) {
        Member user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return false;
        }
        return refreshToken.equals(user.getRefreshToken());
    }

    // DB에 저장된 refreshToken 업데이트
    public void updateRefreshToken(String refreshToken, String userEmail) {
        Member user = userRepository.findByEmail(userEmail);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        }
    }

}
