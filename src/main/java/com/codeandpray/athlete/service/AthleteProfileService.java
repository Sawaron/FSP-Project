package com.codeandpray.athlete.service;

import com.codeandpray.athlete.dto.AthleteProfileRequest;
import com.codeandpray.athlete.dto.AthleteProfileResponse;
import com.codeandpray.athlete.entity.AthleteProfile;
import com.codeandpray.athlete.entity.Organization;
import com.codeandpray.athlete.entity.Qualification;
import com.codeandpray.athlete.mapper.AthleteProfileMapper;
import com.codeandpray.athlete.port.AthleteRating;
import com.codeandpray.athlete.repository.AthleteProfileRepository;
import com.codeandpray.athlete.repository.OrganizationRepository;
import com.codeandpray.athlete.repository.QualificationRepository;
import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.common.security.CurrentActor;
import com.codeandpray.common.web.PageRequests;
import com.codeandpray.common.web.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AthleteProfileService {
    private final AthleteProfileRepository profiles;
    private final OrganizationRepository organizations;
    private final QualificationRepository qualifications;
    private final AthleteProfileMapper mapper;
    private final AthleteRating rating;
    private final CurrentActor actor;
    private final Clock clock;

    @Transactional
    public AthleteProfileResponse createMine(AthleteProfileRequest request) {
        long userId = actor.requireAthleteUserId();
        if (profiles.existsByUserId(userId)) {
            throw BusinessException.conflict("Профиль спортсмена уже создан");
        }
        AthleteProfile profile = AthleteProfile.create(
                userId,
                request.fullName(),
                findOrganization(request.organizationId()),
                request.city(),
                findQualification(request.qualificationId()),
                clock.instant()
        );
        AthleteProfile saved = profiles.saveAndFlush(profile);
        rating.recalculate(saved.getId());
        return mapper.toResponse(saved);
    }

    public AthleteProfileResponse getMine() {
        long userId = actor.requireAthleteUserId();
        return mapper.toResponse(profiles.findByUserId(userId)
                .orElseThrow(() -> BusinessException.notFound("Профиль спортсмена не найден")));
    }

    public AthleteProfileResponse getById(long athleteId) {
        return mapper.toResponse(profiles.findById(athleteId)
                .orElseThrow(() -> BusinessException.notFound("Профиль спортсмена не найден")));
    }

    public PageResponse<AthleteProfileResponse> list(int page, int size) {
        return PageResponse.from(profiles.findAll(
                PageRequests.of(page, size, Sort.by("fullName").ascending().and(Sort.by("id").ascending()))
        ).map(mapper::toResponse));
    }

    @Transactional
    public AthleteProfileResponse updateMine(AthleteProfileRequest request) {
        long userId = actor.requireAthleteUserId();
        AthleteProfile profile = profiles.lockByUserId(userId)
                .orElseThrow(() -> BusinessException.notFound("Профиль спортсмена не найден"));
        Organization organization = findOrganization(request.organizationId());
        Qualification qualification = findQualification(request.qualificationId());
        Long previousQualificationId = idOf(profile.getQualification());
        profile.change(request.fullName(), organization, request.city(), qualification, clock.instant());
        AthleteProfile saved = profiles.saveAndFlush(profile);
        if (!Objects.equals(previousQualificationId, idOf(qualification))) {
            rating.recalculate(saved.getId());
        }
        return mapper.toResponse(saved);
    }

    private Organization findOrganization(Long id) {
        if (id == null) {
            return null;
        }
        return organizations.findById(id)
                .orElseThrow(() -> BusinessException.badRequest("Организация не найдена"));
    }

    private Qualification findQualification(Long id) {
        if (id == null) {
            return null;
        }
        return qualifications.findById(id)
                .orElseThrow(() -> BusinessException.badRequest("Квалификация не найдена"));
    }

    private Long idOf(Qualification qualification) {
        return qualification == null ? null : qualification.getId();
    }
}
