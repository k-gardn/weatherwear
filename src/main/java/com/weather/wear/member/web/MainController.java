package com.weather.wear.member.web;

import com.weather.wear.member.domain.Member;
import com.weather.wear.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MainController.class);
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MemberService memberService;

    @GetMapping("/")
    public String main() {

        return "main";
    }

    @GetMapping("/greeting")
    public String greeting() {

        return "greeting";
    }

    // 모든 회원 조회
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<List<Member>> getAllmembers() {
        List<Member> member = memberService.findAll();
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    //로그인
    @PostMapping("/user/login")
    public Map<String, Object> login(@RequestBody Member login) {
        Map<String, Object> rstMap = new HashMap<>();
        Member user;
        try {
            String email = login.getEmail();
            String password = login.getUserPw();

            System.out.println("email :" + email);
            System.out.println("password :" + login.getUserPw());
            log.debug("email : {}", email);
            log.debug("password : {}", password);

            user = memberService.findByEmail(email);
            System.out.println("user :" + user);
            // 회원일 경우
            if (user != null) {
                // BCryptPasswordEncoder를 사용해 비밀번호를 비교
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//                if(user.getUserPw().equals(login.getUserPw())){
                if (passwordEncoder.matches(password, user.getUserPw())) {
                    // 비밀번호가 맞을 경우
                    rstMap.put("data", user);
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
    public Map<String, Object> userRegister(@RequestBody Member login) {
        Map<String, Object> rstMap = new HashMap<>();
        Member user;
        String email = login.getEmail();
        user = memberService.findByEmail(email);

        System.out.println("password hey : "+login.getUserPw());
        // 기존 회원 -> 이미 가입된 회원입니다.
        if(user != null) rstMap.put("errorMsg", "이미 가입된 회원입니다.");
        // 신규 회원 -> db에 정보 저장.
        else{
            // 비밀번호 암호화
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            System.out.println("password_Check : "+login.getUserPw());
            String encodedPassword = passwordEncoder.encode(login.getUserPw());
            System.out.println("password_Encoding : " +encodedPassword);
            log.debug("비밀번호 : " + login.getUserPw());
            login.setUserPw(encodedPassword);

            //회원정보저장
            memberService.save(login);
            rstMap.put("successMsg", "회원 가입 성공");
        }

        return rstMap;
    }

}



