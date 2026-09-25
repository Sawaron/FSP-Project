package com.codeandpray.result.controller;

import com.codeandpray.common.web.PageResponse;
import com.codeandpray.result.dto.CreateResultRequest;
import com.codeandpray.result.dto.PublishResultRequest;
import com.codeandpray.result.dto.ResultResponse;
import com.codeandpray.result.dto.UpdateResultRequest;
import com.codeandpray.result.service.ResultService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/results")
@lombok.RequiredArgsConstructor
public class ResultController {

    private final ResultService service;

    @GetMapping
    public PageResponse<ResultResponse> listPublished(
            @RequestParam(defaultValue = "0")
            @Min(0) @Max(10000) int page,

            @RequestParam(defaultValue = "20")
            @Min(1) @Max(100) int size
    ) {
        return service.listPublished(page, size);
    }

    @GetMapping("/{id}")
    public ResultResponse getPublished(
            @PathVariable @Positive long id
    ) {
        return service.getPublished(id);
    }

    @GetMapping("/{id}/management")
    public ResultResponse getForOrganizer(
            @PathVariable @Positive long id
    ) {
        return service.getForOrganizer(id);
    }

    @PostMapping
    public ResponseEntity<ResultResponse> create(
            @Valid @RequestBody CreateResultRequest request
    ) {
        ResultResponse response = service.create(request);

        return ResponseEntity.created(
                URI.create("/api/results/" + response.id() + "/management")
        ).body(response);
    }

    @PutMapping("/{id}")
    public ResultResponse updateDraft(
            @PathVariable @Positive long id,
            @Valid @RequestBody UpdateResultRequest request
    ) {
        return service.updateDraft(id, request);
    }

    @PostMapping("/{id}/publish")
    public ResultResponse publish(
            @PathVariable @Positive long id,
            @Valid @RequestBody PublishResultRequest request
    ) {
        return service.publish(id, request);
    }

    @PutMapping("/{id}/correction")
    public ResultResponse correctPublished(
            @PathVariable @Positive long id,
            @Valid @RequestBody UpdateResultRequest request
    ) {
        return service.correctPublished(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDraft(
            @PathVariable @Positive long id,
            @RequestParam @PositiveOrZero long version
    ) {
        service.deleteDraft(id, version);
        return ResponseEntity.noContent().build();
    }
}