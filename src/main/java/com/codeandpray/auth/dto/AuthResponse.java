package com.codeandpray.auth.dto;

import com.codeandpray.auth.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
public record AuthResponse(String token, Long userId, String email, UserRole role) {
    @Override
    public String toString() {
        return "AuthResponse[redacted]";
    }
}