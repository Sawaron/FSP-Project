package com.codeandpray.registration.dto;

public record AthleteSummaryResponse(
        long id,
        String fullName,
        String city,
        String organization,
        String qualification
) {
}
