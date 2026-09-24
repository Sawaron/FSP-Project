package com.codeandpray.rating.mapper;
import com.codeandpray.rating.dto.RatingResponse;
import com.codeandpray.rating.entity.RatingSnapshot;
import org.springframework.stereotype.Component;

@Component
public class RatingMapper {
    public RatingResponse toResponse(RatingSnapshot s) {
        return new RatingResponse(s.getId(), s.getAthleteId(), s.getResultPoints(),
                s.getQualificationPoints(), s.getTotalPoints(), s.getCalculatedAt(), s.getFormulaVersion());
    }
}
