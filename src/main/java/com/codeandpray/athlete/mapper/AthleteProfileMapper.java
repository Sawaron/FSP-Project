package com.codeandpray.athlete.mapper;

import com.codeandpray.athlete.dto.AthleteProfileResponse;
import com.codeandpray.athlete.dto.OrganizationResponse;
import com.codeandpray.athlete.dto.QualificationResponse;
import com.codeandpray.athlete.entity.AthleteProfile;
import com.codeandpray.athlete.entity.Organization;
import com.codeandpray.athlete.entity.Qualification;
import org.springframework.stereotype.Component;

@Component
public class AthleteProfileMapper {
    public AthleteProfileResponse toResponse(AthleteProfile profile) {
        return new AthleteProfileResponse(
                profile.getId(),
                profile.getFullName(),
                toResponse(profile.getOrganization()),
                profile.getCity(),
                toResponse(profile.getQualification()),
                profile.getVersion(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    public OrganizationResponse toResponse(Organization organization) {
        if (organization == null) {
            return null;
        }
        return new OrganizationResponse(organization.getId(), organization.getName(), organization.getCity());
    }

    public QualificationResponse toResponse(Qualification qualification) {
        if (qualification == null) {
            return null;
        }
        return new QualificationResponse(
                qualification.getId(),
                qualification.getName(),
                qualification.getRatingBonus(),
                qualification.getSortOrder()
        );
    }
}
