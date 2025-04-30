package com.weather.wear.member.web;

import com.weather.wear.common.JwtTokenProvider;
import com.weather.wear.common.exception.BaseException;
import com.weather.wear.common.response.ErrorResponseStatus;
import com.weather.wear.common.response.SuccessResponse;
import com.weather.wear.common.response.SuccessStatus;
import com.weather.wear.member.domain.Member;
import com.weather.wear.member.dto.MemberRegister;
import com.weather.wear.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public Map<String, Object> login(@RequestBody Member login) {
        Map<String, Object> rstMap = new HashMap<>();
        Map<String, Object> tokenMap = new HashMap<>();
        Member user;
//        Token token = null;
        try {
            String email = login.getEmail();
            String password = login.getPassword();

            log.debug("email : {}", email);
            log.debug("password : {}", password);

            user = memberService.findByEmail(email);
            // 회원일 경우
            if (user != null) {
                // BCryptPasswordEncoder를 사용해 비밀번호를 비교
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                if (passwordEncoder.matches(password, user.getPassword())) {
                    // 비밀번호가 맞을 경우
                    // 로그인 성공 시 JWT 토큰 생성
                    String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
                    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

                    tokenMap.put("accessToken", accessToken);  // 클라이언트에 JWT 토큰 전달
                    tokenMap.put("refreshToken", refreshToken);  // 클라이언트에 JWT 토큰 전달

                    //db에 refresh token 저장
                    if(refreshToken != null) {
                        user.setRefreshToken(refreshToken); // Transactional로 인해 save를 따로 하지 않아도 db에 저장가능
                    }
                    rstMap.put("data", tokenMap);
                    rstMap.put("success", true);
                } else {
                    // 비밀번호가 틀린 경우
                    rstMap.put("success", false);
                    rstMap.put("errorMsg", "Incorrect password.");
                }
            } else {
                // 회원이 아닌 경우
                rstMap.put("success", false);
                rstMap.put("successMsg", "User not found.");
            }
        } catch (Exception e) {
            log.error("Error during login process", e);
            rstMap.put("success", false);
            rstMap.put("errorMsg", e.getLocalizedMessage());
            e.printStackTrace(System.out);
        }
        return rstMap;
    }

    //회원가입
    @PostMapping("/user/register")
    public ResponseEntity<?> userRegister(@RequestBody @Valid MemberRegister MRDto) {
        String email = MRDto.getEmail();
        Member existingMember = memberService.findByEmail(email);

        // 기존 회원 -> 400에러 리턴
        if (existingMember != null) {
            // 이미 가입된 경우 -> 400 에러 리턴
            throw new BaseException(ErrorResponseStatus.ALREADY_REGISTERED_EMAIL);
        }
        // 신규 회원 -> db에 정보 저장.
        else {
            // 비밀번호 암호화
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(MRDto.getPassword());
//            MRDto.setPassword(encodedPassword);

            //회원정보저장
            Member user = Member.builder()
                    .email(email)
                    .userName(MRDto.getUserName())
                    .userPw(encodedPassword).build();
            memberService.register(user);
            return ResponseEntity.ok(SuccessResponse.of(SuccessStatus.REGISTER_SUCCESS.getMessage()));
        }

    }
    @PostMapping("/user/myPage")
    public Map<String, Object> getMyPage(HttpServletRequest request) {
        Map<String, Object> rstMap = new HashMap<>();
        // Interceptor에서 설정한 사용자 이메일 가져오기
        String userEmail = (String) request.getAttribute("userEmail");
        Member userInfo = memberService.findByEmail(userEmail);
        rstMap.put("data", userInfo);
        rstMap.put("success", true);

        // 사용자 정보를 반환
        return rstMap;
    }


}



