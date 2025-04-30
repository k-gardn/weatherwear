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
    public ResponseEntity<?> login(@RequestBody Member login) {
        String email = login.getEmail();
        String password = login.getPassword();

        log.debug("email : {}", email);
        log.debug("password : {}", password);

        Member user = memberService.findByEmail(email);
        if (user == null) {
            throw new BaseException(ErrorResponseStatus.NOT_FOUND_USER);
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException(ErrorResponseStatus.INCORRECT_PASSWORD);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        if (refreshToken != null) {
            user.setRefreshToken(refreshToken);
        }

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



