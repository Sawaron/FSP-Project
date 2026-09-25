package com.codeandpray.registration.controller;

import com.codeandpray.registration.dto.RegistrationResponse;
import com.codeandpray.registration.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/competitions/{competitionId}/registrations")
    public ResponseEntity<RegistrationResponse> register(@PathVariable Long competitionId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.submitRegistration(competitionId));
    }

    @PutMapping("/registrations/{id}/cancel")
    public ResponseEntity<RegistrationResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.cancelRegistration(id));
    }

    @GetMapping("/athletes/me/registrations")
    public ResponseEntity<List<RegistrationResponse>> getMyRegistrations() {
        return ResponseEntity.ok(registrationService.getMyRegistrations());
    }

    @GetMapping("/competitions/{competitionId}/registrations")
    public ResponseEntity<List<RegistrationResponse>> getCompetitionParticipants(@PathVariable Long competitionId) {
        return ResponseEntity.ok(registrationService.getCompetitionParticipants(competitionId));
    }
}