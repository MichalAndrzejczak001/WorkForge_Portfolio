package com.workforge.applicationservice.infrastructure.messaging;

import com.workforge.applicationservice.domain.event.JobArchivedEvent;
import com.workforge.applicationservice.domain.event.JobDeletedEvent;
import com.workforge.applicationservice.domain.event.JobExpiredEvent;
import com.workforge.applicationservice.domain.event.JobPublishedEvent;
import com.workforge.applicationservice.domain.model.JobCacheStatus;
import com.workforge.applicationservice.domain.model.JobStatusCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.workforge.applicationservice.infrastructure.persistence.JobStatusCacheRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class JobEventConsumer {
    private final JobStatusCacheRepository jobStatusCacheRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "job.published", groupId = "application-service")
    public void handleJobPublished(String message) {
        try {
            JobPublishedEvent event = objectMapper.readValue(message, JobPublishedEvent.class);
            JobStatusCache cache = jobStatusCacheRepository.findById(event.getJobId())
                    .orElse(JobStatusCache.builder().jobId(event.getJobId()).build());

            if (cache.getStatusChangedAt() != null && !event.getOccurredAt().isAfter(cache.getStatusChangedAt())) {
                log.warn("Ignoring stale job.published event for job {}", event.getJobId());
                return;
            }

            cache.setStatus(JobCacheStatus.PUBLISHED);
            cache.setStatusChangedAt(event.getOccurredAt());
            jobStatusCacheRepository.save(cache);
            log.info("Cached job {} as PUBLISHED", event.getJobId());
        } catch (Exception e) {
            log.error("Failed to process job.published event", e);
        }
    }

    @KafkaListener(topics = "job.archived", groupId = "application-service")
    public void handleJobArchived(String message) {
        try {
            JobArchivedEvent event = objectMapper.readValue(message, JobArchivedEvent.class);
            updateStatus(event.getJobId(), JobCacheStatus.ARCHIVED, event.getOccurredAt());
        } catch (Exception e) {
            log.error("Failed to process job.archived event", e);
        }
    }

    @KafkaListener(topics = "job.expired", groupId = "application-service")
    public void handleJobExpired(String message) {
        try {
            JobExpiredEvent event = objectMapper.readValue(message, JobExpiredEvent.class);
            updateStatus(event.getJobId(), JobCacheStatus.EXPIRED, event.getOccurredAt());
        } catch (Exception e) {
            log.error("Failed to process job.expired event", e);
        }
    }

    @KafkaListener(topics = "job.deleted", groupId = "application-service")
    public void handleJobDeleted(String message) {
        try {
            JobDeletedEvent event = objectMapper.readValue(message, JobDeletedEvent.class);
            jobStatusCacheRepository.deleteById(event.getJobId());
            log.info("Deleted job {} from cache", event.getJobId());
        } catch (Exception e) {
            log.error("Failed to process job.deleted event", e);
        }
    }

    private void updateStatus(UUID jobId, JobCacheStatus status, LocalDateTime occurredAt) {
        jobStatusCacheRepository.findById(jobId)
                .ifPresentOrElse(cache -> {
                    if (cache.getStatusChangedAt() != null && !occurredAt.isAfter(cache.getStatusChangedAt())) {
                        log.warn("Ignoring stale event for job {} (status {})", jobId, status);
                        return;
                    }
                    cache.setStatus(status);
                    cache.setStatusChangedAt(occurredAt);
                    jobStatusCacheRepository.save(cache);
                    log.info("Updated job {} status to {}", jobId, status);
                }, () -> log.warn("Job {} not found in cache for status update to {}", jobId, status));
    }
}
