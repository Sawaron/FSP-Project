package com.codeandpray.competition.dto;
import jakarta.validation.constraints.NotBlank;
public record AthleteProfileRequest(
        @NotBlank
        String fullName,
        Long organizationId,
        @NotBlank
        String city,
        Long qualificationId) {
}
