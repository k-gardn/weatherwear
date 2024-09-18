package com.weather.wear.common;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    INVALID_JWT("유효하지 않은 JWT 토큰입니다."),
    EXPIRED_JWT("만료된 JWT 토큰입니다.");

    private final String message;

    BaseResponseStatus(String message) {
        this.message = message;
    }

}
