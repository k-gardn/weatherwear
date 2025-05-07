package com.weather.wear.common.authentication;

import com.weather.wear.common.exception.BaseException;
import com.weather.wear.common.response.ErrorResponseStatus;
import com.weather.wear.member.domain.Member;
import com.weather.wear.member.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class TokenService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

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

    public void saveRefreshToken(String email, String refreshToken, Timestamp expiry) {
        try {
            Member member = userRepository.findByEmail(email);
            if (member == null) {// login Controller에서 이미 검증해서 안해도 될 것 같기는한데,,,
                // 회원이 존재하지 않으면 401 Unauthorized를 던짐
                throw new BaseException(ErrorResponseStatus.NOT_FOUND_USER);
            }
            member.setRefreshToken(refreshToken);
            member.setRefreshTokenExpiry(expiry);
            userRepository.save(member);
            log.debug("[saveRefreshToken] saved successfully");
        } catch (Exception e) {
            log.error("[saveRefreshToken] Exception occurred", e);
            throw new BaseException(ErrorResponseStatus.SAVE_REFRESH_TOKEN_FAILED);
        }
    }

}
