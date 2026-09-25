package com.codeandpray.athlete.repository;

import com.codeandpray.athlete.entity.Qualification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QualificationRepository extends JpaRepository<Qualification, Long> {
    List<Qualification> findAllByOrderBySortOrderAscIdAsc();
}
