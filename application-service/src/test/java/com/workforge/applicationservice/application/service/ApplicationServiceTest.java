package com.workforge.applicationservice.application.service;

import com.workforge.applicationservice.api.dto.request.ChangeStatusRequest;
import com.workforge.applicationservice.api.dto.request.CreateApplicationRequest;
import com.workforge.applicationservice.api.dto.response.ApplicationResponse;
import com.workforge.applicationservice.application.exception.ApplicationNotFoundException;
import com.workforge.applicationservice.domain.model.Application;
import com.workforge.applicationservice.domain.model.ApplicationStatus;
import com.workforge.applicationservice.infrastructure.persistence.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void createApplication_shouldReturnApplicationResponse_whenRequestIsValid() {
        // GIVEN
        UUID jobId = UUID.randomUUID();
        UUID applicantId = UUID.randomUUID();
        CreateApplicationRequest request = CreateApplicationRequest.builder()
                .jobId(jobId)
                .applicantId(applicantId)
                .build();
        Application savedApplication = Application.builder()
                .jobId(jobId)
                .applicantId(applicantId)
                .status(ApplicationStatus.PENDING)
                .build();
        when(applicationRepository.save(any())).thenReturn(savedApplication);

        // WHEN
        ApplicationResponse response = applicationService.createApplication(request);

        // THEN
        assertThat(response.getJobId()).isEqualTo(jobId);
        assertThat(response.getApplicantId()).isEqualTo(applicantId);
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    void getApplication_shouldReturnApplicationResponse_whenApplicationExists() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Application application = Application.builder()
                .jobId(UUID.randomUUID())
                .applicantId(UUID.randomUUID())
                .status(ApplicationStatus.PENDING)
                .build();
        when(applicationRepository.findById(id)).thenReturn(Optional.of(application));

        // WHEN
        ApplicationResponse response = applicationService.getApplication(id);

        // THEN
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    void getApplication_shouldThrowApplicationNotFoundException_whenApplicationDoesNotExist() {
        // GIVEN
        UUID id = UUID.randomUUID();
        when(applicationRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> applicationService.getApplication(id))
                .isInstanceOf(ApplicationNotFoundException.class);
    }

    @Test
    void getApplicationsByJobId_shouldReturnList_whenApplicationsExist() {
        // GIVEN
        UUID jobId = UUID.randomUUID();
        List<Application> applications = List.of(
                Application.builder().jobId(jobId).applicantId(UUID.randomUUID()).status(ApplicationStatus.PENDING).build()
        );
        when(applicationRepository.findByJobId(jobId)).thenReturn(applications);

        // WHEN
        List<ApplicationResponse> response = applicationService.getApplicationsByJobId(jobId);

        // THEN
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getJobId()).isEqualTo(jobId);
    }

    @Test
    void getApplicationsByApplicantId_shouldReturnList_whenApplicationsExist() {
        // GIVEN
        UUID applicantId = UUID.randomUUID();
        List<Application> applications = List.of(
                Application.builder().jobId(UUID.randomUUID()).applicantId(applicantId).status(ApplicationStatus.PENDING).build()
        );
        when(applicationRepository.findByApplicantId(applicantId)).thenReturn(applications);

        // WHEN
        List<ApplicationResponse> response = applicationService.getApplicationsByApplicantId(applicantId);

        // THEN
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getApplicantId()).isEqualTo(applicantId);
    }

    @Test
    void changeStatus_shouldReturnUpdatedResponse_whenApplicationExists() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Application application = Application.builder()
                .jobId(UUID.randomUUID())
                .applicantId(UUID.randomUUID())
                .status(ApplicationStatus.PENDING)
                .build();
        ChangeStatusRequest request = ChangeStatusRequest.builder()
                .status(ApplicationStatus.ACCEPTED)
                .build();
        when(applicationRepository.findById(id)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any())).thenReturn(application);

        // WHEN
        ApplicationResponse response = applicationService.changeStatus(id, request);

        // THEN
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
    }

    @Test
    void changeStatus_shouldThrowApplicationNotFoundException_whenApplicationDoesNotExist() {
        // GIVEN
        UUID id = UUID.randomUUID();
        ChangeStatusRequest request = ChangeStatusRequest.builder()
                .status(ApplicationStatus.ACCEPTED)
                .build();
        when(applicationRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> applicationService.changeStatus(id, request))
                .isInstanceOf(ApplicationNotFoundException.class);
    }
}

