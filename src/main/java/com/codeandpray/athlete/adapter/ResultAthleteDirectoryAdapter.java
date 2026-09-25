package com.codeandpray.athlete.adapter;

import com.codeandpray.athlete.repository.AthleteProfileRepository;
import com.codeandpray.result.port.ResultAthleteDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResultAthleteDirectoryAdapter implements ResultAthleteDirectory {
    private final AthleteProfileRepository repository;

    @Override
    public boolean exists(long athleteId) {
        return repository.existsById(athleteId);
    }
}
