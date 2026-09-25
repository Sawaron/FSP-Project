package com.codeandpray.common.exception;

public class BusinessException extends ApplicationException {
    public BusinessException(String message, org.springframework.http.HttpStatus status) {
        super(message, status, ErrorCode.BAD_REQUEST);
    }
}