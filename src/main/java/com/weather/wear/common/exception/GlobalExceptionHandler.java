package com.weather.wear.common.exception;

import com.weather.wear.common.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Valid 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ConstraintViolationException (예: @RequestParam 유효성 검사 실패)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // BaseException 처리 추가
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        ErrorResponse response = ErrorResponse.of(ex.getStatus());
        return ResponseEntity.status(ex.getStatus().getHttpStatus()).body(response);
    }

    // 그 외 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse response = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
//
//    // 커스텀 예외(BaseException) 처리
//    @ExceptionHandler(BaseException.class)
//    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException ex) {
//        return ResponseEntity
//                .badRequest()
//                .body(BaseResponse.fail(ex.getStatus().name(), ex.getMessage()));
//    }
//
//    // @Valid 관련 예외 처리 (ex: 이메일 누락, 패턴 불일치 등)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<BaseResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
//        String field = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getField();
//        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
//        return ResponseEntity
//                .badRequest()
//                .body(BaseResponse.fail(field.toUpperCase() + "_INVALID", message));
//    }
//
//
//    // 기타 예외 처리
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<BaseResponse<?>> handleAll(Exception ex) {
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(BaseResponse.fail("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
//    }
}
