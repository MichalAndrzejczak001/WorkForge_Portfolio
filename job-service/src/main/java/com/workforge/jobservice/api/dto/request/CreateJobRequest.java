package com.workforge.jobservice.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateJobRequest {
    private String title;
    private String description;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
}
