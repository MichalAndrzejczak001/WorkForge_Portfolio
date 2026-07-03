package com.workforge.jobservice.api.dto.response;

import com.workforge.jobservice.domain.model.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
}
