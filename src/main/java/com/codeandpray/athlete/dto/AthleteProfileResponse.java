package com.codeandpray.athlete.dto;

import java.time.Instant;

public record AthleteProfileResponse(
        long id,
        String fullName,
        OrganizationResponse organization,
        String city,
        QualificationResponse qualification,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
}
