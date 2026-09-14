package com.workforge.applicationservice.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_status_cache")
public class JobStatusCache {
    @Id
    private UUID jobId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobCacheStatus status;
    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime statusChangedAt;
}
