package com.workforge.jobservice.infrastructure.messaging;

import com.workforge.jobservice.domain.event.JobDeletedEvent;
import com.workforge.jobservice.domain.event.JobExpiredEvent;
import com.workforge.jobservice.domain.event.JobPublishedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@AllArgsConstructor
public class JobEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String PUBLISHED_TOPIC = "job.published";
    private static final String DELETED_TOPIC = "job.deleted";
    private static final String EXPIRED_TOPIC = "job.expired";

    public void sendJobPublishedEvent(JobPublishedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(PUBLISHED_TOPIC, event.getJobId().toString(), payload);
        } catch (Exception e) {
            throw new KafkaPublishException("Failed to send Kafka event", e);
        }
    }

    public void sendJobDeletedEvent(JobDeletedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(DELETED_TOPIC, event.getJobId().toString(), payload);
        } catch (Exception e) {
            throw new KafkaPublishException("Failed to send Kafka event", e);
        }
    }

    public void sendJobExpiredEvent(JobExpiredEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(EXPIRED_TOPIC, event.getJobId().toString(), payload);
        } catch (Exception e) {
            throw new KafkaPublishException("Failed to send Kafka event", e);
        }
    }
}


