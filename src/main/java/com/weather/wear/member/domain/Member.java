package com.weather.wear.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Entity(name = "ww_user") // JPA가 관리하는 클래스
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no", nullable = false)
    private Long userNo;

    @Column(name = "user_email", nullable = false, unique = true)
    private String email;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_passwd", nullable = false)
    private String userPw;

    @CreationTimestamp
    @Column(name = "saved_date", updatable = false)
    private Timestamp createdTime;

    @Column(name = "refresh_token")
    private Long refreshToken;

    // 기타 필드들

    // 생성자, 기타 메서드들
// 빌더를 이용한 생성자
    @Builder
    public Member(String email, String userPw, String userName, Long userNo, Timestamp createdTime, Long refreshToken) {
        this.email = email;
        this.userPw = userPw;
        this.userName = userName;
        this.userNo = userNo;
        this.createdTime = createdTime;
        this.refreshToken = refreshToken;
    }

    // 복사 생성자 (선택사항, 필요에 따라 생성)
    public Member(Member member) {
        this.email = member.getEmail();
        this.userPw = member.getUserPw();
        this.userName = member.getUserName();
        this.userNo = member.getUserNo();
        this.createdTime = member.getCreatedTime();
    }


    public boolean checkPassword(String password){
        return this.userPw.equals(password); // 비밀번호 검증
    }
}
