package com.weather.wear.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final boolean success = false;
    private final HttpStatus status;
    private final String message;

    public static ErrorResponse of(ErrorResponseStatus status) {
        return new ErrorResponse(status.getHttpStatus(), status.getMessage());
    }
}