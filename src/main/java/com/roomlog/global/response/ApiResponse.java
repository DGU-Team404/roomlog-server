package com.roomlog.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.roomlog.global.exception.ErrorCode;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final int code;
    private final String message;
    private final T data;
    private final ErrorInfo error;

    private ApiResponse(boolean success, int code, String message, T data, ErrorInfo error) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, "요청 성공", data, null);
    }

    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return new ApiResponse<>(true, code, message, data, null);
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getHttpStatus().value(), errorCode.getMessage(), null,
                new ErrorInfo(errorCode.getCode()));
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode, String message) {
        return new ApiResponse<>(false, errorCode.getHttpStatus().value(), message, null,
                new ErrorInfo(errorCode.getCode()));
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorInfo {
        private final String code;

        public ErrorInfo(String code) {
            this.code = code;
        }
    }
}
