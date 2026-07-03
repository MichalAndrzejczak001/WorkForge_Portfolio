package com.workforge.jobservice.infrastructure.mapper;

import com.workforge.jobservice.api.dto.response.JobResponse;
import com.workforge.jobservice.domain.model.JobOffer;

public class JobMapper {

    public static JobResponse toResponse(JobOffer jobOffer) {
        return JobResponse.builder()
                .id(jobOffer.getId())
                .title(jobOffer.getTitle())
                .description(jobOffer.getDescription())
                .location(jobOffer.getLocation())
                .salaryMin(jobOffer.getSalaryMin())
                .salaryMax(jobOffer.getSalaryMax())
                .status(jobOffer.getStatus())
                .recruiterId(jobOffer.getRecruiterId())
                .createdAt(jobOffer.getCreatedAt())
                .updatedAt(jobOffer.getUpdatedAt())
                .closedAt(jobOffer.getClosedAt())
                .build();
    }
}
