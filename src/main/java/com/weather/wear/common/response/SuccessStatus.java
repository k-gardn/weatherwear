package com.weather.wear.common.response;

import lombok.Getter;

@Getter
public enum SuccessStatus {

    REGISTER_SUCCESS("회원가입 성공"),
    LOGIN_SUCCESS("로그인 성공"),
    LOGOUT_SUCCESS("로그아웃 성공"),
    TOKEN_REISSUE_SUCCESS("토큰 재발급 성공"),
    PROFILE_UPDATE_SUCCESS("프로필 수정 성공"),
    FAVORITE_ADD_SUCCESS("즐겨찾기 추가 성공"),
    FAVORITE_REMOVE_SUCCESS("즐겨찾기 삭제 성공");

    private final String message;

    SuccessStatus(String message) {
        this.message = message;
    }
}