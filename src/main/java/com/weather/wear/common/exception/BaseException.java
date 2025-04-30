package com.weather.wear.common.exception;

import com.weather.wear.common.response.ErrorResponseStatus;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private final ErrorResponseStatus status;

    // 생성자에서 BaseResponseStatus를 받아서 설정
    public BaseException(ErrorResponseStatus status) {
        super(status.getMessage()); // 예외 메시지를 설정
        this.status = status;
    }

}
