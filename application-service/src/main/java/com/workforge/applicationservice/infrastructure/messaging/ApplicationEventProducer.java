package com.workforge.applicationservice.infrastructure.messaging;

import com.workforge.applicationservice.domain.event.ApplicationSubmittedEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class ApplicationEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String SUBMITTED_TOPIC = "application.submitted";

    public void sendApplicationSubmittedEvent(ApplicationSubmittedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(SUBMITTED_TOPIC, event.getApplicationId().toString(), payload);
        } catch (Exception e) {
            throw new KafkaPublishException("Failed to send kafka event", e);
        }
    }
}
