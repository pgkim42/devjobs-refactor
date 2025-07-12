package com.example.devjobs.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // Null인 필드는 JSON으로 변환하지 않음
public class ApiResponse<T> {

    private final int statusCode;
    private final String message;
    private final T data;

    private ApiResponse(HttpStatus status, String message, T data) {
        this.statusCode = status.value();
        this.message = message;
        this.data = data;
    }

    // 성공 응답 (데이터 포함)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK, "성공적으로 처리되었습니다.", data);
    }

    // 성공 응답 (데이터 미포함, 예: 삭제)
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(HttpStatus.OK, "성공적으로 처리되었습니다.", null);
    }
    
    // 생성(Created) 성공 응답
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(HttpStatus.CREATED, "성공적으로 생성���었습니다.", data);
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
