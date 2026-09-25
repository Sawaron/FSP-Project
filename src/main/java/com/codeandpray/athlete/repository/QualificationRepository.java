package com.codeandpray.athlete.repository;


import com.codeandpray.athlete.Qualification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualificationRepository extends JpaRepository<Qualification, Long> {
}
