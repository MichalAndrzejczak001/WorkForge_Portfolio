package com.workforge.applicationservice.domain.event;

import com.workforge.applicationservice.domain.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusChangedEvent {
    private UUID applicationId;
    private UUID jobId;
    private UUID applicantId;
    private ApplicationStatus oldStatus;
    private ApplicationStatus newStatus;
    private LocalDateTime changedAt;
}
