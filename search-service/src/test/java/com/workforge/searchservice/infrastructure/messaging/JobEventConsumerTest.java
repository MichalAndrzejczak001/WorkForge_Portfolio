package com.workforge.searchservice.infrastructure.messaging;

import com.workforge.searchservice.domain.document.JobOfferDocument;
import com.workforge.searchservice.infrastructure.persistence.JobSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobEventConsumerTest {
    @Mock
    private JobSearchRepository jobSearchRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JobEventConsumer jobEventConsumer;

    @Test
    void handleJobPublished_shouldSaveDocument_whenMessageIsValid() {
        // GIVEN
        String message = "{}";
        JobOfferDocument document = JobOfferDocument.builder()
                .title("Java Developer")
                .build();
        when(objectMapper.readValue(message, JobOfferDocument.class))
                .thenReturn(document);

        // WHEN
        jobEventConsumer.handleJobPublished(message);

        // THEN
        verify(jobSearchRepository).save(document);
    }

    @Test
    void handleJobPublished_shouldNotThrow_whenMessageIsInvalid() {
        // GIVEN
        String message = "{invalid json}";
        when(objectMapper.readValue(message, JobOfferDocument.class))
                .thenThrow(new RuntimeException("Invalid json"));

        // WHEN/THEN
        assertThatCode(() -> jobEventConsumer.handleJobPublished(message)).doesNotThrowAnyException();
        verify(jobSearchRepository, never()).save(any());

    }
}
