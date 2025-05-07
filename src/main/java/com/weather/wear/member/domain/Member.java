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
    private String password;

    @CreationTimestamp
    @Column(name = "saved_date", updatable = false)
    private Timestamp createdTime;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_token_expiry")
    private Timestamp refreshTokenExpiry;

    @Column(name = "user_def_location")
    private String defaultLocation;

    // 기타 필드들

    // 생성자, 기타 메서드들
// 빌더를 이용한 생성자
    @Builder
    public Member(String email, String userPw, String userName, Long userNo,
                  Timestamp createdTime, String refreshToken, Timestamp refreshTokenExpiry,
                  String defaultLocation) {
        this.email = email;
        this.password = userPw;
        this.userName = userName;
        this.userNo = userNo;
        this.createdTime = createdTime;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiry = refreshTokenExpiry;
        this.defaultLocation = defaultLocation;
    }

    public boolean checkPassword(String password){
        return this.password.equals(password); // 비밀번호 검증
    }
}
