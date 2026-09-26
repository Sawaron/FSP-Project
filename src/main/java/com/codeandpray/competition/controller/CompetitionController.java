package com.codeandpray.competition.controller;

import com.codeandpray.common.web.PageResponse;
import com.codeandpray.competition.dto.*;
import com.codeandpray.competition.enums.CompetitionStatus;
import com.codeandpray.competition.service.CompetitionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competitions")
@lombok.RequiredArgsConstructor
public class CompetitionController {
    private final CompetitionService service;

    @GetMapping("/mine")
    public PageResponse<CompetitionResponse> mine(
            @RequestParam(defaultValue = "0") @Min(0) @Max(10000) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return service.mine(page, size);
    }

    @GetMapping("/{id}/management")
    public CompetitionResponse management(@PathVariable @Positive long id) {
        return service.management(id);
    }

    @GetMapping
    public PageResponse<CompetitionResponse> list(@RequestParam(required = false) CompetitionStatus status,
                                                  @RequestParam(required = false) @Positive Long disciplineId,
                                                  @RequestParam(defaultValue = "0") @Min(0) @Max(10000) int page,
                                                  @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return service.list(status, disciplineId, page, size);
    }

    @GetMapping("/{id}")
    public CompetitionResponse get(@PathVariable @Positive long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<CompetitionResponse> create(@Valid @RequestBody CreateCompetitionRequest request) {
        CompetitionResponse result = service.create(request);
        return ResponseEntity.created(URI.create("/api/competitions/" + result.id())).body(result);
    }

    @PutMapping("/{id}")
    public CompetitionResponse update(@PathVariable @Positive long id, @Valid @RequestBody UpdateCompetitionRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public CompetitionResponse status(@PathVariable @Positive long id, @Valid @RequestBody ChangeCompetitionStatusRequest request) {
        return service.changeStatus(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive long id, @RequestParam @PositiveOrZero long version) {
        service.delete(id, version);
        return ResponseEntity.noContent().build();
    }
}
