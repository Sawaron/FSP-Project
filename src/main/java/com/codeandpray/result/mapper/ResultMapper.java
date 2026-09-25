package com.codeandpray.result.mapper;

import com.codeandpray.result.dto.ResultResponse;
import com.codeandpray.result.entity.Result;
import org.springframework.stereotype.Component;

@Component
public class ResultMapper {

    public ResultResponse toResponse(Result result) {
        return new ResultResponse(
                result.getId(),
                result.getVersion(),
                result.getRegistrationId(),
                result.getPlace(),
                result.getPerformanceValue(),
                result.getPerformanceUnit(),
                result.getStatus(),
                result.getRatingPoints(),
                result.getFormulaVersion(),
                result.getPublishedAt(),
                result.getCreatedAt(),
                result.getUpdatedAt()
        );
    }
}