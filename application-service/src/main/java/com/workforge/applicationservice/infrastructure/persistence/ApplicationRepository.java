package com.workforge.applicationservice.infrastructure.persistence;

import com.workforge.applicationservice.domain.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByJobId(UUID jobId);
    List<Application> findByApplicantId(UUID applicantId);
}
