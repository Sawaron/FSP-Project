package com.codeandpray.contest.dto;

import com.codeandpray.contest.port.ContestResults.Standing;
import java.time.Instant;
import java.util.List;

public record StandingsResponse(long competitionId, Instant finalizedAt, List<Standing> participants) {}

