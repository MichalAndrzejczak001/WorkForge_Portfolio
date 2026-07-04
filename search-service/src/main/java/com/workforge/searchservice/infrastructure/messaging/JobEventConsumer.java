package com.workforge.searchservice.infrastructure.messaging;

import com.workforge.searchservice.domain.document.JobOfferDocument;
import com.workforge.searchservice.infrastructure.persistence.JobSearchRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@AllArgsConstructor
public class JobEventConsumer {
    private final JobSearchRepository jobSearchRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "job.published", groupId = "search-service")
    public void handleJobPublished(String message) {
        try  {
            JobOfferDocument document = objectMapper.readValue(message, JobOfferDocument.class);
            jobSearchRepository.save(document);
            log.info("Indexed job offer: {}", document.getId());
        } catch (Exception e) {
            log.error("Failed to process Kafka message", e);
        }
    }
}
