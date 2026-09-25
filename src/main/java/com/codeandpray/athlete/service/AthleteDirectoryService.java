package com.codeandpray.athlete.service;

import com.codeandpray.athlete.dto.DisciplineResponse;
import com.codeandpray.athlete.dto.OrganizationResponse;
import com.codeandpray.athlete.dto.QualificationResponse;
import com.codeandpray.athlete.mapper.AthleteProfileMapper;
import com.codeandpray.athlete.repository.DisciplineRepository;
import com.codeandpray.athlete.repository.OrganizationRepository;
import com.codeandpray.athlete.repository.QualificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AthleteDirectoryService {
    private final OrganizationRepository organizations;
    private final QualificationRepository qualifications;
    private final DisciplineRepository disciplines;
    private final AthleteProfileMapper mapper;

    public List<OrganizationResponse> organizations() {
        return organizations.findAllByOrderByNameAscIdAsc().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<QualificationResponse> qualifications() {
        return qualifications.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<DisciplineResponse> disciplines() {
        return disciplines.findAllByOrderByNameAscIdAsc().stream()
                .map(discipline -> new DisciplineResponse(
                        discipline.getId(), discipline.getName(), discipline.getDescription()))
                .toList();
    }
}
