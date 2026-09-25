package com.codeandpray.auth.dto;

import com.codeandpray.auth.enums.UserRole;

public record AuthResponse(String token, Long userId, String email, UserRole role) {
    @Override
    public String toString() {
        return "AuthResponse[redacted]";
    }
}