package com.workforge.applicationservice.api.dto.request;

import com.workforge.applicationservice.domain.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeStatusRequest {
    @NotNull
    private ApplicationStatus status;
}
