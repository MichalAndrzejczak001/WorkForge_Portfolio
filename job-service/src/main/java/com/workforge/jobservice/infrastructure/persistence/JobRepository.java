package com.workforge.jobservice.infrastructure.persistence;

import com.workforge.jobservice.domain.model.JobOffer;
import com.workforge.jobservice.domain.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<JobOffer, UUID> {
    Page<JobOffer> findByRecruiterId(UUID recruiterId, Pageable pageable);
    Page<JobOffer> findByStatus(JobStatus status, Pageable pageable);
    List<JobOffer> findByStatusAndExpiresAtBefore(JobStatus status, LocalDateTime dateTime);
}
