package com.codeandpray.rating.controller;

import com.codeandpray.common.web.PageResponse;
import com.codeandpray.rating.dto.RatingResponse;
import com.codeandpray.rating.service.RatingService;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@lombok.RequiredArgsConstructor
public class RatingController {
    private final RatingService service;

    @GetMapping
    public PageResponse<RatingResponse> leaderboard(@RequestParam(defaultValue = "0") @Min(0) @Max(10000) int page,
                                                    @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return service.leaderboard(page, size);
    }

    @GetMapping("/athletes/{athleteId}")
    public RatingResponse current(@PathVariable @Positive long athleteId) {
        return service.current(athleteId);
    }

    @GetMapping("/athletes/{athleteId}/history")
    public PageResponse<RatingResponse> history(@PathVariable @Positive long athleteId,
                                                @RequestParam(defaultValue = "0") @Min(0) @Max(10000) int page,
                                                @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return service.history(athleteId, page, size);
    }
}