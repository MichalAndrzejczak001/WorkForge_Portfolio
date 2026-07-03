package com.workforge.jobservice.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_offers")
public class JobOffer {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String title;
    private String description;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    @Column(nullable = false)
    private UUID recruiterId;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
}
