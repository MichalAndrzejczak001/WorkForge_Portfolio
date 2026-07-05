package com.workforge.applicationservice.infrastructure.mapper;


import com.workforge.applicationservice.api.dto.response.ApplicationResponse;
import com.workforge.applicationservice.domain.model.Application;

public class ApplicationMapper {

    public static ApplicationResponse toResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJobId())
                .applicantId(application.getApplicantId())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}
