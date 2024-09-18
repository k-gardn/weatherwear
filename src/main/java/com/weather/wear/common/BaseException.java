package com.weather.wear.common;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private final BaseResponseStatus status;

    // 생성자에서 BaseResponseStatus를 받아서 설정
    public BaseException(BaseResponseStatus status) {
        super(status.getMessage()); // 예외 메시지를 설정
        this.status = status;
    }

}
