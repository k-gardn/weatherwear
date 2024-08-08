package com.weather.wear.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity(name = "ww_users") // JPA가 관리하는 클래스
public class Member {

    @Id //기본키 PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성
    private Long user_no;

    private String email;
    private String password;
    private Timestamp joined;

    // 기타 필드들

    // 생성자, 기타 메서드들
    @Builder
    public Member(String email, String password){
        this.email = email;
        this.password = password;
    }
}
