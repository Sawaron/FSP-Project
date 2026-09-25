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

    @PostMapping
    public ResponseEntity<AthleteProfileDto> create(
            @RequestParam Long userId,
            @Valid @RequestBody AthleteProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AthleteProfileDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AthleteProfileDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AthleteProfileDto> update(
            @PathVariable Long id,
            @Valid @RequestBody AthleteProfileRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}