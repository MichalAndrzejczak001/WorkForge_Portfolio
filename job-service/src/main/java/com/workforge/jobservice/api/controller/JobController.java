package com.workforge.jobservice.api.controller;

import com.workforge.jobservice.api.dto.request.CreateJobRequest;
import com.workforge.jobservice.api.dto.request.UpdateJobRequest;
import com.workforge.jobservice.api.dto.response.JobResponse;
import com.workforge.jobservice.api.dto.response.JobStatsResponse;
import com.workforge.jobservice.application.service.JobService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@AllArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody CreateJobRequest request,
                                                 @RequestHeader("X-User-Id") UUID recruiterId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.createJob(request, recruiterId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getJob(id));
    }

    @GetMapping
    public ResponseEntity<Page<JobResponse>> getAllJobs(Pageable pageable) {
        return ResponseEntity.ok(jobService.getAllJobs(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateJobRequest request,
                                                 @RequestHeader("X-User-Id") UUID recruiterId) {
        return ResponseEntity.ok(jobService.updateJob(id, request, recruiterId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable UUID id,
                                          @RequestHeader("X-User-Id") UUID recruiterId) {
        jobService.deleteJob(id, recruiterId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<JobResponse> publishJob(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.publishJob(id));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<JobResponse> archiveJob(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.archiveJob(id));
    }

    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<Page<JobResponse>> getJobsByRecruiterId(@PathVariable UUID recruiterId, Pageable pageable) {
        return ResponseEntity.ok(jobService.getJobsByRecruiterId(recruiterId, pageable));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<JobStatsResponse> getJobStats(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getJobsStats(id));
    }

}
