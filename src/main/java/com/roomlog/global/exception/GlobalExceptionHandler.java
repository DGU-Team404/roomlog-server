package com.roomlog.global.exception;

import com.roomlog.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = fieldError != null ? fieldError.getDefaultMessage() : ErrorCode.COMMON_400.getMessage();
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(ErrorCode.COMMON_400, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity
                .status(ErrorCode.COMMON_500.getHttpStatus())
                .body(ApiResponse.failure(ErrorCode.COMMON_500));
    }
}
