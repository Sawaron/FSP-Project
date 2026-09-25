package com.codeandpray.competition.dto;

public record AthleteProfileDto(String fullName,
                                Long organizationId,
                                String city,
                                Long qualificationId) {
}
