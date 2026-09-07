package com.workforge.jobservice.application.service;

import com.workforge.jobservice.api.dto.request.CreateJobRequest;
import com.workforge.jobservice.api.dto.request.UpdateJobRequest;
import com.workforge.jobservice.api.dto.response.JobResponse;
import com.workforge.jobservice.application.exception.InvalidJobStatusException;
import com.workforge.jobservice.application.exception.JobAccessDeniedException;
import com.workforge.jobservice.application.exception.JobNotFoundException;
import com.workforge.jobservice.domain.event.JobDeletedEvent;
import com.workforge.jobservice.domain.event.JobPublishedEvent;
import com.workforge.jobservice.domain.model.JobOffer;
import com.workforge.jobservice.domain.model.JobStatus;
import com.workforge.jobservice.infrastructure.mapper.JobMapper;
import com.workforge.jobservice.infrastructure.messaging.JobEventProducer;
import com.workforge.jobservice.infrastructure.persistence.JobRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .companyName(createJobRequest.getCompanyName())
                .workMode(createJobRequest.getWorkMode())
                .experienceLevel(createJobRequest.getExperienceLevel())
                .skills(createJobRequest.getSkills())
                .expiresAt(createJobRequest.getExpiresAt())
                .build();

        JobOffer savedOffer = jobRepository.save(jobOffer);

        return JobMapper.toResponse(savedOffer);

    }

    @Transactional(readOnly = true)
    public JobResponse getJob(UUID id) {
        JobOffer existingJobOffer = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job with id " + id + " doesn't exist."));

        return JobMapper.toResponse(existingJobOffer);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs() {
        return jobRepository.findByStatus(JobStatus.PUBLISHED).stream()
                .map(JobMapper::toResponse)
                .toList();
    }

    @Transactional
    public JobResponse updateJob(UUID id, UpdateJobRequest updateJobRequest, UUID recruiterId) {
        JobOffer jobOffer = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job with id " + id + " doesn't exist."));

        if (!jobOffer.getRecruiterId().equals(recruiterId)) {
            throw new JobAccessDeniedException("You are not allowed to modify this job offer.");
        }
        if (jobOffer.getStatus() != JobStatus.DRAFT) {
            throw new InvalidJobStatusException("Only draft job offers can be updated.");
        }

        jobOffer.setTitle(updateJobRequest.getTitle());
        jobOffer.setDescription(updateJobRequest.getDescription());
        jobOffer.setLocation(updateJobRequest.getLocation());
        jobOffer.setSalaryMin(updateJobRequest.getSalaryMin());
        jobOffer.setSalaryMax(updateJobRequest.getSalaryMax());
        jobOffer.setCompanyName(updateJobRequest.getCompanyName());
        jobOffer.setWorkMode(updateJobRequest.getWorkMode());
        jobOffer.setExperienceLevel(updateJobRequest.getExperienceLevel());
        jobOffer.setSkills(updateJobRequest.getSkills());
        jobOffer.setExpiresAt(updateJobRequest.getExpiresAt());

        JobOffer savedOffer = jobRepository.save(jobOffer);

        return JobMapper.toResponse(savedOffer);
    }

    public void deleteJob(UUID id, UUID recruiterId) {
        JobOffer jobOffer = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job with id " + id + " doesn't exist."));

        if (!jobOffer.getRecruiterId().equals(recruiterId)) {
            throw new JobAccessDeniedException("You are not allowed to delete this job offer.");
        }
        if (!jobOffer.getStatus().equals(JobStatus.DRAFT)) {
            throw new InvalidJobStatusException("Only draft job offers can be deleted.");
        }

        jobRepository.delete(jobOffer);

        JobDeletedEvent event = JobDeletedEvent.builder()
                .jobId(jobOffer.getId())
                .build();
        jobEventProducer.sendJobDeletedEvent(event);
    }

    @Transactional
    public JobResponse publishJob(UUID id) {
        JobOffer jobOffer = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job with id " + id + " doesn't exist."));

        jobOffer.setStatus(JobStatus.PUBLISHED);
        jobOffer.setPublishedAt(LocalDateTime.now());

        JobOffer savedOffer = jobRepository.save(jobOffer);

        JobPublishedEvent event = JobPublishedEvent.builder()
                .jobId(savedOffer.getId())
                .title(savedOffer.getTitle())
                .description(savedOffer.getDescription())
                .location(savedOffer.getLocation())
                .salaryMin(savedOffer.getSalaryMin())
                .salaryMax(savedOffer.getSalaryMax())
                .recruiterId(savedOffer.getRecruiterId())
                .build();
        jobEventProducer.sendJobPublishedEvent(event);

        return JobMapper.toResponse(savedOffer);
    }

    @Transactional
    public JobResponse archiveJob(UUID id) {
        JobOffer jobOffer = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job with id " + id + " doesn't exist."));

        jobOffer.setStatus(JobStatus.ARCHIVED);
        jobOffer.setClosedAt(LocalDateTime.now());

        JobOffer savedOffer = jobRepository.save(jobOffer);

        return JobMapper.toResponse(savedOffer);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getJobsByRecruiterId(UUID recruiterId) {
        return jobRepository.findByRecruiterId(recruiterId).stream()
                .map(JobMapper::toResponse)
                .toList();
    }
}
