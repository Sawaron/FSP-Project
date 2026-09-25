package com.codeandpray.athlete.controller;

import com.codeandpray.athlete.dto.DisciplineResponse;
import com.codeandpray.athlete.dto.OrganizationResponse;
import com.codeandpray.athlete.dto.QualificationResponse;
import com.codeandpray.athlete.service.AthleteDirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AthleteDirectoryController {
    private final AthleteDirectoryService service;

    @GetMapping("/organizations")
    public List<OrganizationResponse> organizations() {
        return service.organizations();
    }

    @GetMapping("/qualifications")
    public List<QualificationResponse> qualifications() {
        return service.qualifications();
    }

    @GetMapping("/disciplines")
    public List<DisciplineResponse> disciplines() {
        return service.disciplines();
    }
}
