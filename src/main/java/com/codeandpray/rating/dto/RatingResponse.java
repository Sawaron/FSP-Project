package com.codeandpray.rating.dto;

import java.time.Instant;

public record RatingResponse(
        long snapshotId,
        long athleteId,
        long resultPoints,
        long qualificationPoints,
        long totalPoints,
        Instant calculatedAt,
        int formulaVersion) {
}
