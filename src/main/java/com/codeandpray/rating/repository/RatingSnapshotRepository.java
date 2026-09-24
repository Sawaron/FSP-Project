package com.codeandpray.rating.repository;
import com.codeandpray.rating.entity.RatingSnapshot;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface RatingSnapshotRepository extends JpaRepository<RatingSnapshot, Long> {
    Optional<RatingSnapshot> findFirstByAthleteIdOrderByIdDesc(long athleteId);
    Page<RatingSnapshot> findByAthleteId(long athleteId, Pageable page);

    @Query
            (value = "select s from RatingSnapshot s where not exists (select newer.id from RatingSnapshot newer where newer.athleteId = s.athleteId and newer.id > s.id)",
           countQuery = "select count(s) from RatingSnapshot s where not exists (select newer.id from RatingSnapshot newer where newer.athleteId = s.athleteId and newer.id > s.id)")
    Page<RatingSnapshot> findLatestForEachAthlete(Pageable page);
}
