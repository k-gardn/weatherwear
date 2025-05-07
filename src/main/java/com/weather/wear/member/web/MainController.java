package com.weather.wear.member.web;

import com.weather.wear.common.authentication.JwtTokenProvider;
import com.weather.wear.common.authentication.TokenService;
import com.weather.wear.common.authentication.dto.TokenRefreshRequest;
import com.weather.wear.common.authentication.dto.TokenRefreshResponse;
import com.weather.wear.common.exception.BaseException;
import com.weather.wear.common.response.ErrorResponseStatus;
import com.weather.wear.common.response.SuccessResponse;
import com.weather.wear.common.response.SuccessStatus;
import com.weather.wear.member.domain.Member;
import com.weather.wear.member.dto.PasswordChangeRequest;
import com.weather.wear.member.dto.MemberRegister;
import com.weather.wear.member.service.MemberService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class MainController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MemberService memberService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    TokenService tokenService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String main() {

        return "main";
    }

    // 모든 회원 조회
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<List<Member>> getAllmembers() {
        List<Member> member = memberService.findAll();
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    //로그인
    @Transactional
    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody Member login) {
        String email = login.getEmail();
        String password = login.getPassword();

        // 사용자 조회
        Member user = memberService.findByEmail(email);
        if (user == null) {
            throw new BaseException(ErrorResponseStatus.NOT_FOUND_USER);
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException(ErrorResponseStatus.INCORRECT_PASSWORD);
        }

        // accessToken 발급
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());

        // refreshToken → 기존 유효한 것 사용 or 새 발급
        String refreshToken;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (user.getRefreshToken() != null && user.getRefreshTokenExpiry() != null &&
                user.getRefreshTokenExpiry().after(now)) {
            // 기존 refreshToken 유효 → 재사용
            log.debug("refreshToken 유효 → 재사용 : {}", user.getRefreshTokenExpiry());
            refreshToken = user.getRefreshToken();
        } else {
            // 없거나 만료 → 새로 발급
            refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
            tokenService.saveRefreshToken(email, refreshToken, jwtTokenProvider.getExpiry(refreshToken));
        }
        // 응답
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        return ResponseEntity.ok(SuccessResponse.of(tokenMap));
    }

    //회원가입
    @PostMapping("/user/register")
    public ResponseEntity<?> userRegister(@RequestBody @Valid MemberRegister MRDto) {
        String email = MRDto.getEmail();
        Member existingMember = memberService.findByEmail(email);

        if (existingMember != null) {
            // 이미 가입된 경우 -> 400 에러 리턴
            throw new BaseException(ErrorResponseStatus.ALREADY_REGISTERED_EMAIL);
        }
        // 신규 회원 -> db에 정보 저장.
        else {
            // 비밀번호 암호화
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(MRDto.getPassword());

            //회원정보저장
            Member user = Member.builder()
                    .email(email)
                    .userName(MRDto.getUserName())
                    .userPw(encodedPassword).build();
            memberService.register(user);
            return ResponseEntity.ok(SuccessResponse.of(SuccessStatus.REGISTER_SUCCESS.getMessage()));
        }

    }

    @PostMapping("/user/me")
        public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal String userEmail) {
        Member userInfo = memberService.findByEmail(userEmail);
        if (userInfo == null) {
            throw new BaseException(ErrorResponseStatus.FORBIDDEN);
        }
        // 사용자 정보를 반환
        return ResponseEntity.ok(SuccessResponse.of(userInfo));
    }

    // 엑세스 토큰이 만료되었을 경우, 리프레시 토큰을 보내서 엑세스 토큰을 발급해주는 api
    @PostMapping("/user/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // 리프레시 토큰이 기한 만료된 경우.
        if (!jwtTokenProvider.validateToken(refreshToken, false)) {
            throw new BaseException(ErrorResponseStatus.UNAUTHORIZED); // 유효하지 않음
        }

        String email = jwtTokenProvider.getUserEmail(refreshToken, false);
        // DB에 있는 refreshToken과 비교.
        if (!tokenService.validateRefreshToken(email, refreshToken)) {
            throw new BaseException(ErrorResponseStatus.UNAUTHORIZED); // DB와 다름
        }
        // DB와 같은 경우 새로운 accessToken 발급.
        String newAccessToken = jwtTokenProvider.generateAccessToken(email);
        return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken));
    }

    @PostMapping("/user/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal String userEmail) {
//        String email = userDetails.getUsername(); // 또는 JwtTokenProvider로 email 추출
        boolean deleted = tokenService.deleteRefreshToken(userEmail);
        if (!deleted) {
            throw new BaseException(ErrorResponseStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(SuccessResponse.of("로그아웃 완료"));
    }

    @PostMapping("/user/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal String userEmail,
                                            @RequestBody PasswordChangeRequest request) {
        Member member = memberService.findByEmail(userEmail);

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new BaseException(ErrorResponseStatus.INCORRECT_PASSWORD);
        }

        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        member.setRefreshToken(null); // refreshToken 제거
        member.setRefreshTokenExpiry(null);
        memberService.register(member);

        return ResponseEntity.ok(SuccessResponse.of("비밀번호 변경 완료 → 다시 로그인 필요"));
    }
}



