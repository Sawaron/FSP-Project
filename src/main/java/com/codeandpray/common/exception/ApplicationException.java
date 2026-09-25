package com.codeandpray.common.exception;

import org.springframework.http.HttpStatus;

public class ApplicationException extends RuntimeException {
    private final HttpStatus status;
    private final ErrorCode code;

    public ApplicationException(String message, HttpStatus status, ErrorCode code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() { return status; }
    public ErrorCode getCode() { return code; }

    public static ApplicationException badRequest(String message) {
        return new ApplicationException(message, HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST);
    }
    public static ApplicationException notFound(String message) {
        return new ApplicationException(message, HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND);
    }
    public static ApplicationException conflict(String message) {
        return new ApplicationException(message, HttpStatus.CONFLICT, ErrorCode.CONFLICT);
    }
    public static ApplicationException forbidden(String message) {
        return new ApplicationException(message, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN);
    }
    public static ApplicationException unauthorized(String message) {
        return new ApplicationException(message, HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
    }
}