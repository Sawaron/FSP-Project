package com.codeandpray.registration.controller;

import com.codeandpray.common.web.PageResponse;
import com.codeandpray.registration.dto.RegistrationResponse;
import com.codeandpray.registration.service.RegistrationService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService service;

    @PostMapping("/competitions/{competitionId}/registrations")
    public ResponseEntity<RegistrationResponse> register(@PathVariable @Positive Long competitionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.submitRegistration(competitionId));
    }

    @PutMapping("/registrations/{id}/cancel")
    public RegistrationResponse cancel(@PathVariable @Positive Long id) {
        return service.cancelRegistration(id);
    }

    @GetMapping("/athletes/me/registrations")
    public PageResponse<RegistrationResponse> mine(
            @RequestParam(defaultValue = "0") @Min(0) @Max(10000) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return service.getMyRegistrations(page, size);
    }

    @GetMapping("/competitions/{competitionId}/registrations")
    public PageResponse<RegistrationResponse> participants(
            @PathVariable @Positive Long competitionId,
            @RequestParam(defaultValue = "0") @Min(0) @Max(10000) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return service.getCompetitionParticipants(competitionId, page, size);
    }
}