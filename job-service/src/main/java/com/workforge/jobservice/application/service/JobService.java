package com.workforge.jobservice.application.service;

import com.workforge.jobservice.api.dto.request.CreateJobRequest;
import com.workforge.jobservice.api.dto.request.UpdateJobRequest;
import com.workforge.jobservice.api.dto.response.JobResponse;
import com.workforge.jobservice.application.exception.JobNotFoundException;
import com.workforge.jobservice.domain.event.JobPublishedEvent;
import com.workforge.jobservice.domain.model.JobOffer;
import com.workforge.jobservice.domain.model.JobStatus;
import com.workforge.jobservice.infrastructure.mapper.JobMapper;
import com.workforge.jobservice.infrastructure.messaging.JobEventProducer;
import com.workforge.jobservice.infrastructure.persistence.JobRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobEventProducer jobEventProducer;

    public JobResponse createJob(CreateJobRequest createJobRequest, UUID recruiterId) {
        JobOffer jobOffer = JobOffer.builder()
                .title(createJobRequest.getTitle())
                .description(createJobRequest.getDescription())
                .location(createJobRequest.getLocation())
                .salaryMin(createJobRequest.getSalaryMin())
                .salaryMax(createJobRequest.getSalaryMax())
                .status(JobStatus.DRAFT)
                .recruiterId(recruiterId)
                .build();

        JobOffer savedOffer = jobRepository.save(jobOffer);

        return JobMapper.toResponse(savedOffer);

    }

    public JobResponse getJob(UUID id) {
        JobOffer existingJobOffer = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job with id " + id + " doesn't exist."));

        return JobMapper.toResponse(existingJobOffer);
    }

    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(JobMapper::toResponse)
                .toList();
    }

    public JobResponse updateJob(UUID id, UpdateJobRequest updateJobRequest) {
        JobOffer jobOffer = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job with id " + id + " doesn't exist."));

        jobOffer.setTitle(updateJobRequest.getTitle());
        jobOffer.setDescription(updateJobRequest.getDescription());
        jobOffer.setLocation(updateJobRequest.getLocation());
        jobOffer.setSalaryMin(updateJobRequest.getSalaryMin());
        jobOffer.setSalaryMax(updateJobRequest.getSalaryMax());

        JobOffer savedOffer = jobRepository.save(jobOffer);

        return JobMapper.toResponse(savedOffer);
    }

    public void deleteJob(UUID id) {
        JobOffer jobOffer = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job with id " + id + " doesn't exist."));
        jobRepository.delete(jobOffer);
    }

    public JobResponse changeStatus(UUID id, JobStatus jobStatus) {
        JobOffer jobOffer = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job with id " + id + " doesn't exist."));

        jobOffer.setStatus(jobStatus);

        if (jobStatus.equals(JobStatus.CLOSED)) {
            jobOffer.setClosedAt(LocalDateTime.now());
        }

        JobOffer savedOffer = jobRepository.save(jobOffer);

        if (jobStatus.equals(JobStatus.PUBLISHED)) {
            JobPublishedEvent event = JobPublishedEvent.builder()
                    .jobId(savedOffer.getId())
                    .title(savedOffer.getTitle())
                    .location(savedOffer.getLocation())
                    .salaryMin(savedOffer.getSalaryMin())
                    .salaryMax(savedOffer.getSalaryMax())
                    .recruiterId(savedOffer.getRecruiterId())
                    .build();
            jobEventProducer.sendJobPublishedEvent(event);
        }

        return JobMapper.toResponse(savedOffer);
    }

}
