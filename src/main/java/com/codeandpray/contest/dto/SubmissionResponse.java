package com.codeandpray.contest.dto;

import com.codeandpray.contest.entity.Submission;
import java.time.Instant;

public record SubmissionResponse(long id, long version, long taskId, long registrationId,
                                 String answer, Instant submittedAt, String status,
                                 Integer points, String reviewComment, Long reviewedBy, Instant reviewedAt) {
    public static SubmissionResponse from(Submission s) {
        return new SubmissionResponse(s.getId(), s.getVersion(), s.getTaskId(), s.getRegistrationId(),
                s.getAnswer(), s.getSubmittedAt(), s.getPoints() == null ? "SUBMITTED" : "REVIEWED",
                s.getPoints(), s.getReviewComment(), s.getReviewedBy(), s.getReviewedAt());
    }
}

