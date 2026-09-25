package com.codeandpray.athlete.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AthleteProfileRequest(
        @NotBlank @Size(max = 255) String fullName,
        @Positive Long organizationId,
        @NotBlank @Size(max = 255) String city,
        @Positive Long qualificationId
) {
}
