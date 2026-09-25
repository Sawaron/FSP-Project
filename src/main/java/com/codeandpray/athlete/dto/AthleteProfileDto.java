package com.codeandpray.athlete.dto;

public record AthleteProfileDto(
        Long id,
        String fullName,
        Long organizationId,
        String city,
        Long qualificationId
) {
}