package com.workforge.applicationservice.domain.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID jobId;
    @Column(nullable = false)
    private UUID applicantId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;
    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
