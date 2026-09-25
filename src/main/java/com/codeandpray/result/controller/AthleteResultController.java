package com.codeandpray.result.controller;

import com.codeandpray.common.web.PageResponse;
import com.codeandpray.result.dto.ResultResponse;
import com.codeandpray.result.service.ResultService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/athletes/{athleteId}/results")
@RequiredArgsConstructor
public class AthleteResultController {
    private final ResultService service;

    @GetMapping
    public PageResponse<ResultResponse> list(
            @PathVariable @Positive long athleteId,
            @RequestParam(defaultValue = "0") @Min(0) @Max(10000) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return service.listPublishedByAthlete(athleteId, page, size);
    }
}
