package com.workforge.jobservice.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank
    private String title;
    private String description;
    @NotBlank
    private String location;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salaryMin;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salaryMax;
}
