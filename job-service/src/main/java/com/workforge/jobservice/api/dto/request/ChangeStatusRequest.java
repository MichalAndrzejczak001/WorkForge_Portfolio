package com.workforge.jobservice.api.dto.request;

import com.workforge.jobservice.domain.model.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeStatusRequest {
    private JobStatus status;
}

