package com.codeandpray.registration.dto;

import com.codeandpray.registration.enums.RegistrationStatus;

import java.time.Instant;

public record RegistrationResponse(
        Long id,
        Long competitionId,
        Long athleteId,
        RegistrationStatus status,
        Instant registeredAt
) {}