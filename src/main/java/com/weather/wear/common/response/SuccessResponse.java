package com.weather.wear.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponse<T> {
    private final boolean success = true;
    private final T data;

    public static <T> SuccessResponse<T> of(T data) {
        return new SuccessResponse<>(data);
    }

    public static SuccessResponse<Void> empty() {
        return new SuccessResponse<>(null);
    }
}