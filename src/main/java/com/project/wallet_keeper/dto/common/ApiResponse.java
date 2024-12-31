package com.project.wallet_keeper.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private String status; // 상태명
    private int code; // 상태 코드
    private String message; // 응답 메시지
    private T data; // 응답 데이터

    public static <T> ApiResponse<T> success(HttpStatus status) {
        return createResponse(status, "Success", null);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return createResponse(status, "Success", data);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, String message) {
        return createResponse(status, message, null);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        return createResponse(status, message, data);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return createResponse(status, message, null);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, T data) {
        return createResponse(status, message, data);
    }

    private static <T> ApiResponse<T> createResponse(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status.name(), status.value(), message, data);
    }
}
