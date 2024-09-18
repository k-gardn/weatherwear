package com.weather.wear.member.service;

import com.weather.wear.member.domain.Member;
import com.weather.wear.member.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {
    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private TokenRepository tokenRepository;

    public List<Member> findAll(){
        List<Member> members = new ArrayList<>();
        userRepository.findAll();
        return members;
    }

    public Member findByEmail(String email){
//        Member user;
//        user = userRepository.findByEmail(email);
        return userRepository.findByEmail(email);
    }

    public void register(Member member){
        userRepository.save(member);
    }

//    public void saveRefToken(Token token){
//        tokenRepository.save(token);
//    }


}
