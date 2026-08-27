package com.workforge.jobservice.api.dto.response;

import com.workforge.jobservice.domain.model.ExperienceLevel;
import com.workforge.jobservice.domain.model.JobStatus;
import com.workforge.jobservice.domain.model.WorkMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {
    private UUID id;
    private String title;
    private String description;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private JobStatus status;
    private UUID recruiterId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
    private String companyName;
    private WorkMode workMode;
    private ExperienceLevel experienceLevel;
    private List<String> skills;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;
}
