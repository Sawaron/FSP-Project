package com.codeandpray.athlete.controller;

import com.codeandpray.athlete.dto.AthleteProfileRequest;
import com.codeandpray.athlete.dto.AthleteProfileResponse;
import com.codeandpray.athlete.service.AthleteProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/athletes")
@RequiredArgsConstructor
public class AthleteProfileController {
    private final AthleteProfileService service;

    @PostMapping("/me/profile")
    @ResponseStatus(HttpStatus.CREATED)
    public AthleteProfileResponse createMine(@Valid @RequestBody AthleteProfileRequest request) {
        return service.createMine(request);
    }

    @GetMapping("/me/profile")
    public AthleteProfileResponse getMine() {
        return service.getMine();
    }

    @PutMapping("/me/profile")
    public AthleteProfileResponse updateMine(@Valid @RequestBody AthleteProfileRequest request) {
        return service.updateMine(request);
    }

    @GetMapping("/{athleteId}")
    public AthleteProfileResponse getById(@PathVariable @Positive long athleteId) {
        return service.getById(athleteId);
    }
}
