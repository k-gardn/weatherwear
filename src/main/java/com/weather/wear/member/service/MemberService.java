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

    public List<Member> findAll(){
        List<Member> members = new ArrayList<>();
        userRepository.findAll();
        return members;
    }

}
