package com.codeandpray.result.repository;

import com.codeandpray.result.entity.Result;
import com.codeandpray.result.enums.ResultStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {

    boolean existsByRegistrationId(long registrationId);

    Optional<Result> findByRegistrationId(long registrationId);

    Optional<Result> findByIdAndStatus(long id, ResultStatus status);

    Page<Result> findByStatus(ResultStatus status, Pageable pageable);

    @Query("select r.registrationId from Result r where r.id = :id")
    Optional<Long> findRegistrationIdById(@Param("id") long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Result r where r.id = :id")
    Optional<Result> findForUpdate(@Param("id") long id);
}