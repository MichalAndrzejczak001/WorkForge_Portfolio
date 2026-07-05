package com.workforge.applicationservice.application.service;

import com.workforge.applicationservice.api.dto.request.ChangeStatusRequest;
import com.workforge.applicationservice.api.dto.request.CreateApplicationRequest;
import com.workforge.applicationservice.api.dto.response.ApplicationResponse;
import com.workforge.applicationservice.application.exception.ApplicationNotFoundException;
import com.workforge.applicationservice.domain.model.Application;
import com.workforge.applicationservice.domain.model.ApplicationStatus;
import com.workforge.applicationservice.infrastructure.mapper.ApplicationMapper;
import com.workforge.applicationservice.infrastructure.persistence.ApplicationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public ApplicationResponse createApplication(CreateApplicationRequest request) {
        Application application = Application.builder()
                .jobId(request.getJobId())
                .applicantId(request.getApplicantId())
                .status(ApplicationStatus.PENDING)
                .build();

        Application savedApplication = applicationRepository.save(application);

        return ApplicationMapper.toResponse(savedApplication);
    }

    public ApplicationResponse getApplication(UUID id){
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application with id " + id + " doesn't exist."));
        return ApplicationMapper.toResponse(application);
    }

    public List<ApplicationResponse> getApplicationsByJobId(UUID jobId){
        List<Application> applications = applicationRepository.findByJobId(jobId);

        return applications.stream().map(ApplicationMapper::toResponse).toList();
    }

    public List<ApplicationResponse> getApplicationsByApplicantId(UUID applicantId) {
        List<Application> applications = applicationRepository.findByApplicantId(applicantId);
        return applications.stream().map(ApplicationMapper::toResponse).toList();
    }

    public ApplicationResponse changeStatus(UUID applicationId, ChangeStatusRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application with id " + applicationId + " doesn't exist."));
        application.setStatus(request.getStatus());
        application.setUpdatedAt(LocalDateTime.now());
        Application savedApplication = applicationRepository.save(application);
        return ApplicationMapper.toResponse(savedApplication);
    }
}
