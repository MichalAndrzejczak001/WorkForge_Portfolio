package com.workforge.jobservice.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.workforge.jobservice.api.dto.request.CreateJobRequest;
import com.workforge.jobservice.api.dto.request.UpdateJobRequest;
import com.workforge.jobservice.api.dto.response.JobResponse;
import com.workforge.jobservice.application.exception.InvalidJobStatusException;
import com.workforge.jobservice.application.exception.JobAccessDeniedException;
import com.workforge.jobservice.application.exception.JobNotFoundException;
import com.workforge.jobservice.domain.model.ExperienceLevel;
import com.workforge.jobservice.domain.model.JobOffer;
import com.workforge.jobservice.domain.model.JobStatus;
import com.workforge.jobservice.domain.model.WorkMode;
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
    void publishJob_shouldSendKafkaEvent() {
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
        jobService.publishJob(id);

        //THEN
        verify(jobEventProducer).sendJobPublishedEvent(any());
    }

    @Test
    void archiveJob_shouldSetArchivedStatusWithoutSendingKafkaEvent() {
        //GIVEN
        UUID id = UUID.randomUUID();
        JobOffer jobOffer = JobOffer.builder()
                .id(id)
                .title("Java Developer")
                .location("Warszawa")
                .salaryMin(new BigDecimal("8000"))
                .salaryMax(new BigDecimal("12000"))
                .status(JobStatus.PUBLISHED)
                .recruiterId(UUID.randomUUID())
                .build();

        when(jobRepository.findById(id)).thenReturn(Optional.of(jobOffer));
        when(jobRepository.save(any(JobOffer.class))).thenReturn(jobOffer);

        //WHEN
        JobResponse result = jobService.archiveJob(id);

        //THEN
        assertThat(result.getStatus()).isEqualTo(JobStatus.ARCHIVED);
        verify(jobEventProducer, never()).sendJobPublishedEvent(any());
    }

    @Test
    void updateJob_shouldUpdateFields_whenOwnerAndDraft() {
        //GIVEN
        UUID id = UUID.randomUUID();
        UUID recruiterId = UUID.randomUUID();
        JobOffer jobOffer = JobOffer.builder()
                .id(id)
                .title("Old Title")
                .location("Warszawa")
                .salaryMin(new BigDecimal("8000"))
                .salaryMax(new BigDecimal("12000"))
                .status(JobStatus.DRAFT)
                .recruiterId(recruiterId)
                .build();

        UpdateJobRequest request = UpdateJobRequest.builder()
                .title("New Title")
                .location("Krakow")
                .salaryMin(new BigDecimal("9000"))
                .salaryMax(new BigDecimal("13000"))
                .workMode(WorkMode.REMOTE)
                .experienceLevel(ExperienceLevel.MID)
                .build();

        when(jobRepository.findById(id)).thenReturn(Optional.of(jobOffer));
        when(jobRepository.save(any(JobOffer.class))).thenReturn(jobOffer);

        //WHEN
        JobResponse result = jobService.updateJob(id, request, recruiterId);

        //THEN
        assertThat(result.getTitle()).isEqualTo("New Title");
    }

    @Test
    void updateJob_shouldThrowJobAccessDeniedException_whenNotOwner() {
        //GIVEN
        UUID id = UUID.randomUUID();
        JobOffer jobOffer = JobOffer.builder()
                .id(id)
                .status(JobStatus.DRAFT)
                .recruiterId(UUID.randomUUID())
                .build();

        UpdateJobRequest request = UpdateJobRequest.builder().build();

        when(jobRepository.findById(id)).thenReturn(Optional.of(jobOffer));

        //WHEN / THEN
        assertThatThrownBy(() -> jobService.updateJob(id, request, UUID.randomUUID()))
                .isInstanceOf(JobAccessDeniedException.class);
    }

    @Test
    void updateJob_shouldThrowInvalidJobStatusException_whenNotDraft() {
        //GIVEN
        UUID id = UUID.randomUUID();
        UUID recruiterId = UUID.randomUUID();
        JobOffer jobOffer = JobOffer.builder()
                .id(id)
                .status(JobStatus.PUBLISHED)
                .recruiterId(recruiterId)
                .build();

        UpdateJobRequest request = UpdateJobRequest.builder().build();

        when(jobRepository.findById(id)).thenReturn(Optional.of(jobOffer));

        // WHEN / THEN
        assertThatThrownBy(() -> jobService.updateJob(id, request, recruiterId))
                .isInstanceOf(InvalidJobStatusException.class);
    }

    @Test
    void deleteJob_shouldDeleteJob_whenOwnerAndDraft() {
        //GIVEN
        UUID id = UUID.randomUUID();
        UUID recruiterId = UUID.randomUUID();
        JobOffer jobOffer = JobOffer.builder()
                .id(id)
                .status(JobStatus.DRAFT)
                .recruiterId(recruiterId)
                .build();

        when(jobRepository.findById(id)).thenReturn(Optional.of(jobOffer));

        //WHEN
        jobService.deleteJob(id, recruiterId);

        //THEN
        verify(jobRepository).delete(jobOffer);
    }

    @Test
    void deleteJob_shouldThrowJobAccessDeniedException_whenNotOwner() {
        //GIVEN
        UUID id = UUID.randomUUID();
        JobOffer jobOffer = JobOffer.builder()
                .id(id)
                .status(JobStatus.DRAFT)
                .recruiterId(UUID.randomUUID())
                .build();

        when(jobRepository.findById(id)).thenReturn(Optional.of(jobOffer));

        // WHEN / THEN
        assertThatThrownBy(() -> jobService.deleteJob(id, UUID.randomUUID()))
                .isInstanceOf(JobAccessDeniedException.class);
    }

    @Test
    void deleteJob_shouldThrowInvalidJobStatusException_whenNotDraft() {
        //GIVEN
        UUID id = UUID.randomUUID();
        UUID recruiterId = UUID.randomUUID();
        JobOffer jobOffer = JobOffer.builder()
                .id(id)
                .status(JobStatus.PUBLISHED)
                .recruiterId(recruiterId)
                .build();

        when(jobRepository.findById(id)).thenReturn(Optional.of(jobOffer));

        //WHEN / THEN
        assertThatThrownBy(() -> jobService.deleteJob(id, recruiterId))
                .isInstanceOf(InvalidJobStatusException.class);
    }
}
