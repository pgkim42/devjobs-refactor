package com.example.devjobs.common.exception;

/**
 * 요청한 리소스를 찾을 수 없을 때 사용되는 예외입니다.
 * (예: 존재하지 않는 사용자 조회 시도)
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}