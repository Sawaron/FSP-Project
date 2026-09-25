package com.codeandpray.athlete.repository;


import com.codeandpray.athlete.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}