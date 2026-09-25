package com.codeandpray.athlete.controller;


import com.codeandpray.athlete.AthleteProfileRequest;
import com.codeandpray.athlete.dto.AthleteProfileDto;
import com.codeandpray.athlete.service.AthleteProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/athlete-profiles")
public class AthleteProfileController {

    private final AthleteProfileService service;

    public AthleteProfileController(AthleteProfileService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AthleteProfileDto> create(
            Authentication authentication,
            @Valid @RequestBody AthleteProfileRequest request) {

        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(userId, request));
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