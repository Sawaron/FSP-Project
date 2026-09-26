package com.codeandpray.contest.dto;

import jakarta.validation.constraints.*;

public record SubmissionRequest(@NotBlank @Size(max = 50000) String answer) {}

