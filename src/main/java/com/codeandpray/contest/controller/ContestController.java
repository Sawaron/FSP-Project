package com.codeandpray.contest.controller;

import com.codeandpray.common.web.PageResponse;
import com.codeandpray.contest.dto.*;
import com.codeandpray.contest.service.ContestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contests/{competitionId}")
@RequiredArgsConstructor
public class ContestController {
    private final ContestService service;

    @GetMapping("/tasks")
    public List<TaskResponse> tasks(@PathVariable @Positive long competitionId) {
        return service.tasks(competitionId, false);
    }

    @GetMapping("/management/tasks")
    public List<TaskResponse> managementTasks(@PathVariable @Positive long competitionId) {
        return service.tasks(competitionId, true);
    }

    @PostMapping("/management/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@PathVariable @Positive long competitionId, @Valid @RequestBody TaskRequest request) {
        return service.createTask(competitionId, request);
    }

    @PutMapping("/management/tasks/{taskId}")
    public TaskResponse updateTask(@PathVariable @Positive long competitionId, @PathVariable @Positive long taskId,
                                   @RequestParam @PositiveOrZero long version, @Valid @RequestBody TaskRequest request) {
        return service.updateTask(competitionId, taskId, version, request);
    }

    @DeleteMapping("/management/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable @Positive long competitionId, @PathVariable @Positive long taskId,
                           @RequestParam @PositiveOrZero long version) {
        service.deleteTask(competitionId, taskId, version);
    }

    @PostMapping("/tasks/{taskId}/submissions")
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponse submit(@PathVariable @Positive long competitionId, @PathVariable @Positive long taskId,
                                     @Valid @RequestBody SubmissionRequest request) {
        return service.submit(competitionId, taskId, request);
    }

    @GetMapping("/submissions/mine")
    public List<SubmissionResponse> mine(@PathVariable @Positive long competitionId) {
        return service.mine(competitionId);
    }

    @GetMapping("/management/submissions")
    public PageResponse<SubmissionResponse> submissions(@PathVariable @Positive long competitionId,
            @RequestParam(required = false) @Positive Long taskId,
            @RequestParam(required = false) Boolean reviewed,
            @RequestParam(defaultValue = "0") @Min(0) @Max(10000) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return service.submissions(competitionId, taskId, reviewed, page, size);
    }

    @GetMapping("/management/submissions/{submissionId}")
    public SubmissionResponse submission(@PathVariable @Positive long competitionId, @PathVariable @Positive long submissionId) {
        return service.submission(competitionId, submissionId);
    }

    @PutMapping("/management/submissions/{submissionId}/review")
    public SubmissionResponse review(@PathVariable @Positive long competitionId, @PathVariable @Positive long submissionId,
                                     @Valid @RequestBody ReviewRequest request) {
        return service.review(competitionId, submissionId, request);
    }

    @PostMapping("/management/finalize")
    public StandingsResponse finalizeResults(@PathVariable @Positive long competitionId) {
        return service.finalizeResults(competitionId);
    }

    @GetMapping("/standings")
    public StandingsResponse standings(@PathVariable @Positive long competitionId) {
        return service.standings(competitionId);
    }
}

