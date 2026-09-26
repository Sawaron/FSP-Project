package com.codeandpray.contest.dto;

import com.codeandpray.contest.entity.ContestTask;

public record TaskResponse(long id, long version, String title, String statement, int maxPoints, int sortOrder) {
    public static TaskResponse from(ContestTask t) {
        return new TaskResponse(t.getId(), t.getVersion(), t.getTitle(), t.getStatement(), t.getMaxPoints(), t.getSortOrder());
    }
}

