package com.weather.wear.member.web;

import com.weather.wear.member.domain.Member;
import com.weather.wear.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class MainController {

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


    
}



