package com.workforge.jobservice.infrastructure.persistence;

import com.workforge.jobservice.domain.model.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<JobOffer, UUID> {
    List<JobOffer> findByRecruiterId(UUID recruiterId);
}
