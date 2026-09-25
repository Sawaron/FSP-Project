package com.codeandpray.athlete.service;

import com.codeandpray.athlete.*;
import com.codeandpray.athlete.dto.AthleteProfileDto;
import com.codeandpray.athlete.repository.AthleteProfileRepository;
import com.codeandpray.athlete.repository.OrganizationRepository;
import com.codeandpray.athlete.repository.QualificationRepository;
import com.codeandpray.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AthleteProfileService {

    private final AthleteProfileRepository repository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final QualificationRepository qualificationRepository;

    public AthleteProfileService(
            AthleteProfileRepository repository,
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            QualificationRepository qualificationRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.qualificationRepository = qualificationRepository;
    }

    @Transactional
    public AthleteProfileDto create(Long userId, AthleteProfileRequest request) {
        AthleteProfile profile = new AthleteProfile();
        profile.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId)));
        profile.setFullName(request.fullName());
        profile.setCity(request.city());

        if (request.organizationId() != null) {
            profile.setOrganization(organizationRepository.findById(request.organizationId())
                    .orElseThrow(() -> new IllegalArgumentException("Organization not found")));
        }
        if (request.qualificationId() != null) {
            profile.setQualification(qualificationRepository.findById(request.qualificationId())
                    .orElseThrow(() -> new IllegalArgumentException("Qualification not found")));
        }

        profile.setCreatedAt(Instant.now());
        profile.setUpdatedAt(Instant.now());

        AthleteProfile saved = repository.save(profile);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public AthleteProfileDto getById(Long id) {
        AthleteProfile profile = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + id));
        return toDto(profile);
    }

    @Transactional(readOnly = true)
    public List<AthleteProfileDto> getAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public AthleteProfileDto update(Long id, AthleteProfileRequest request) {
        AthleteProfile profile = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + id));

        profile.setFullName(request.fullName());
        profile.setCity(request.city());
        profile.setUpdatedAt(Instant.now());

        if (request.organizationId() != null) {
            profile.setOrganization(organizationRepository.findById(request.organizationId())
                    .orElseThrow(() -> new IllegalArgumentException("Organization not found")));
        }
        if (request.qualificationId() != null) {
            profile.setQualification(qualificationRepository.findById(request.qualificationId())
                    .orElseThrow(() -> new IllegalArgumentException("Qualification not found")));
        }

        return toDto(repository.save(profile));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Profile not found: " + id);
        }
        repository.deleteById(id);
    }

    private AthleteProfileDto toDto(AthleteProfile p) {
        return new AthleteProfileDto(
                p.getFullName(),
                p.getOrganization() != null ? p.getOrganization().getId() : null,
                p.getCity(),
                p.getQualification() != null ? p.getQualification().getId() : null
        );
    }
}
