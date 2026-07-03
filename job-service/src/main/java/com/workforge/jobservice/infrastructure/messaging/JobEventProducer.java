package com.workforge.jobservice.infrastructure.messaging;

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

    private static final String TOPIC = "job.published";

    public void sendJobPublishedEvent(JobPublishedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.getJobId().toString(), payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send Kafka event", e);
        }
    }
}


