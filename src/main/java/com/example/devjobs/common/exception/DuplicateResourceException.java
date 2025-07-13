package com.example.devjobs.common.exception;

/**
 * 리소스가 이미 존재하여 충돌이 발생할 때 사용되는 예외입니다.
 * (예: 이미 사용중인 아이디로 회원가입 시도)
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
