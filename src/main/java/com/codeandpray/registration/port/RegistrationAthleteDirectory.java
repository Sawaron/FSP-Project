package com.codeandpray.registration.port;

import java.util.Map;
import java.util.Set;

public interface RegistrationAthleteDirectory {
    Map<Long, AthleteSummary> findAll(Set<Long> athleteIds);

    record AthleteSummary(
            long id,
            String fullName,
            String city,
            String organization,
            String qualification
    ) {
    }
}
