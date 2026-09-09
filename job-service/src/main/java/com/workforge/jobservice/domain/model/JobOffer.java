package com.workforge.jobservice.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    private String companyName;
    @Enumerated(EnumType.STRING)
    private WorkMode workMode;
    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;
    @ElementCollection
    @CollectionTable(name = "job_offer_skills", joinColumns = @JoinColumn(name = "job_offer_id"))
    @Column(name = "skill")
    private List<String> skills;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
    @Column(nullable = false)
    private Long viewsCount;
    @Column(nullable = false)
    private Long applicationsCount;
}
