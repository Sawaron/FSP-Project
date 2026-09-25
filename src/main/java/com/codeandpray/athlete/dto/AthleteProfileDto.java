package com.codeandpray.athlete.dto;

public record AthleteProfileDto(String fullName,
                                Long organizationId,
                                String city,
                                Long qualificationId) {
}
