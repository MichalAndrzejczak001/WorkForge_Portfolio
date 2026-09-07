package com.workforge.jobservice.infrastructure.messaging;

public class KafkaPublishException extends RuntimeException {
    public KafkaPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
