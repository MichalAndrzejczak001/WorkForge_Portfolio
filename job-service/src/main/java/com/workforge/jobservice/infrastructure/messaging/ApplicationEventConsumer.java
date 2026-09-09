package com.workforge.jobservice.infrastructure.messaging;

import com.workforge.jobservice.domain.event.ApplicationSubmittedEvent;
import com.workforge.jobservice.infrastructure.persistence.JobRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@AllArgsConstructor
public class ApplicationEventConsumer {
    private final JobRepository jobRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "application.submitted", groupId = "job-service")
    public void handleApplicationSubmitted(String message) {
        try {
            ApplicationSubmittedEvent event = objectMapper.readValue(message, ApplicationSubmittedEvent.class);

            jobRepository.findById(event.getJobId())
                    .ifPresentOrElse(jobOffer -> {
                        jobOffer.setApplicationsCount(jobOffer.getApplicationsCount() + 1);
                        jobRepository.save(jobOffer);
                        log.info("Incremented applicationsCount for job: {}", event.getJobId());
                    }, () -> log.warn("Job {} not found for application event", event.getJobId()));
        } catch (Exception e) {
            log.error("Failed to process Kafka message", e);
        }
    }
}
