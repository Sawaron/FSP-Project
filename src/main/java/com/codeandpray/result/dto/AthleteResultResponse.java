package com.codeandpray.result.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AthleteResultResponse(long id, long registrationId, long competitionId, String competitionTitle,
                                    Instant competitionStartsAt, Integer place, BigDecimal performanceValue,
                                    String performanceUnit, long ratingPoints, Instant publishedAt, String status) {}
