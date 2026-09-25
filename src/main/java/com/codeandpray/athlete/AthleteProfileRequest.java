package com.codeandpray.athlete;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AthleteProfileRequest(

        @NotBlank
        String fullName,

        Long organizationId,

        @NotBlank
        String city,

        Long qualificationId
) {
}