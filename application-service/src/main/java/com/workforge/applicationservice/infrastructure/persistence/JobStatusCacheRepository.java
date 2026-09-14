package com.workforge.applicationservice.infrastructure.persistence;

import com.workforge.applicationservice.domain.model.JobStatusCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobStatusCacheRepository extends JpaRepository<JobStatusCache, UUID> {

}
