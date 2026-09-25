package com.codeandpray.athlete.repository;

import com.codeandpray.athlete.entity.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisciplineRepository extends JpaRepository<Discipline, Long> {
    List<Discipline> findAllByOrderByNameAscIdAsc();
}
