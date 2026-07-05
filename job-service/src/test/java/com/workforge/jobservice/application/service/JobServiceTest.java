package com.workforge.jobservice.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;

import com.workforge.jobservice.api.dto.request.CreateJobRequest;
import com.workforge.jobservice.api.dto.response.JobResponse;
import com.workforge.jobservice.application.exception.JobNotFoundException;
import com.workforge.jobservice.domain.model.JobOffer;
import com.workforge.jobservice.domain.model.JobStatus;
import com.workforge.jobservice.infrastructure.messaging.JobEventProducer;
import com.workforge.jobservice.infrastructure.persistence.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {
    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobEventProducer jobEventProducer;

    @InjectMocks
    private JobService jobService;

    @Test
    void createJob_shouldCreateJobWithDraftStatus() {
        // GIVEN
        UUID recruiterId = UUID.randomUUID();
        CreateJobRequest request = CreateJobRequest.builder()
                .title("Java Developer")
                .location("Warszawa")
                .salaryMin(new BigDecimal("8000"))
                .salaryMax(new BigDecimal("12000"))
                .build();

        JobOffer savedOffer = JobOffer.builder()
                .id(UUID.randomUUID())
                .title("Java Developer")
                .location("Warszawa")
                .salaryMin(new BigDecimal("8000"))
                .salaryMax(new BigDecimal("12000"))
                .status(JobStatus.DRAFT)
                .recruiterId(recruiterId)
                .build();

        when(jobRepository.save(any(JobOffer.class))).thenReturn(savedOffer);

        // WHEN
        JobResponse result = jobService.createJob(request, recruiterId);

        // THEN
        assertThat(result.getStatus()).isEqualTo(JobStatus.DRAFT);
        assertThat(result.getTitle()).isEqualTo("Java Developer");
    }

    @Test
    void getJob_shouldThrowJobNotFoundException_whenJobDoesNotExist() {
        //GIVEN
        UUID id = UUID.randomUUID();
        when(jobRepository.findById(id)).thenReturn(Optional.empty());

        //WHEN / THEN
        assertThatThrownBy(() -> jobService.getJob(id))
                .isInstanceOf(JobNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void changeStatus_shouldSendKafkaEvent_whenStatusChangedToPublished() {
        //GIVEN
        UUID id = UUID.randomUUID();
        JobOffer jobOffer = JobOffer.builder()
                .id(id)
                .title("Java Developer")
                .location("Warszawa")
                .salaryMin(new BigDecimal("8000"))
                .salaryMax(new BigDecimal("12000"))
                .status(JobStatus.DRAFT)
                .recruiterId(UUID.randomUUID())
                .build();

        when(jobRepository.findById(id)).thenReturn(Optional.of(jobOffer));
        when(jobRepository.save(any(JobOffer.class))).thenReturn(jobOffer);

        //WHEN
        jobService.changeStatus(id, JobStatus.PUBLISHED);

        //THEN
        verify(jobEventProducer).sendJobPublishedEvent(any());
    }
}
