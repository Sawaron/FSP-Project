package com.codeandpray.athlete.adapter;

import com.codeandpray.athlete.entity.AthleteProfile;
import com.codeandpray.athlete.repository.AthleteProfileRepository;
import com.codeandpray.registration.port.RegistrationAthleteDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegistrationAthleteDirectoryAdapter implements RegistrationAthleteDirectory {
    private final AthleteProfileRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Map<Long, AthleteSummary> findAll(Set<Long> athleteIds) {
        if (athleteIds.isEmpty()) {
            return Map.of();
        }
        return repository.findAllByIdIn(athleteIds).stream()
                .map(this::toSummary)
                .collect(Collectors.toUnmodifiableMap(AthleteSummary::id, Function.identity()));
    }

    private AthleteSummary toSummary(AthleteProfile profile) {
        return new AthleteSummary(
                profile.getId(),
                profile.getFullName(),
                profile.getCity(),
                profile.getOrganization() == null ? null : profile.getOrganization().getName(),
                profile.getQualification() == null ? null : profile.getQualification().getName()
        );
    }
}
