package com.codeandpray.common.exception;

import java.time.Instant;

public record ApiError(
        int status,
        ErrorCode code,
        String message,
        Instant timestamp
) {
    public static ApiError of(int status, ErrorCode code, String message) {
        return new ApiError(status, code, message, Instant.now());
    }
}