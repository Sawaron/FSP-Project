package com.codeandpray.registration.service;

import com.codeandpray.competition.port.CompetitionParticipation;
import com.codeandpray.registration.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompetitionParticipationAdapter implements CompetitionParticipation {

    private final RegistrationRepository registrationRepository;

    @Override
    public boolean hasAnyRegistration(long competitionId) {
        return registrationRepository.existsByCompetitionId(competitionId);
    }
}
