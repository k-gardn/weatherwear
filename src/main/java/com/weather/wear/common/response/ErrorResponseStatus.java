package com.weather.wear.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

//    에러 메세지를 enum으로 관리하기.
@Getter
public enum ErrorResponseStatus {

    // JWT 관련 에러
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    SAVE_REFRESH_TOKEN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "리프레시 토큰 저장에 실패했습니다."),

    // 회원가입 관련 에러
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호 형식이 유효하지 않습니다."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다."),
    ALREADY_REGISTERED_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    PASSWORD_TOO_SIMPLE(HttpStatus.BAD_REQUEST, "비밀번호는 특수문자, 숫자 포함 8자 이상이어야 합니다."),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    // 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),

    // 404
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),

    // 기타
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorResponseStatus(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
