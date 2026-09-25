package com.codeandpray.common.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends ApplicationException {
    public BusinessException(String message, HttpStatus status) {
        super(message, status, switch (status) {
            case BAD_REQUEST -> ErrorCode.BAD_REQUEST;
            case UNAUTHORIZED -> ErrorCode.UNAUTHORIZED;
            case FORBIDDEN -> ErrorCode.FORBIDDEN;
            case NOT_FOUND -> ErrorCode.NOT_FOUND;
            case CONFLICT -> ErrorCode.CONFLICT;
            default -> ErrorCode.INTERNAL_ERROR;
        });
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(message, HttpStatus.BAD_REQUEST);
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(message, HttpStatus.UNAUTHORIZED);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(message, HttpStatus.FORBIDDEN);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(message, HttpStatus.NOT_FOUND);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(message, HttpStatus.CONFLICT);
    }
}