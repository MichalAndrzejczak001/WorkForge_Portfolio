package com.workforge.jobservice.api.controller;

import com.workforge.jobservice.api.dto.request.ChangeStatusRequest;
import com.workforge.jobservice.api.dto.request.CreateJobRequest;
import com.workforge.jobservice.api.dto.request.UpdateJobRequest;
import com.workforge.jobservice.api.dto.response.JobResponse;
import com.workforge.jobservice.application.service.JobService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateJobRequest request) {
        return ResponseEntity.ok(jobService.updateJob(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable UUID id) {
        jobService.deleteJob(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobResponse> changeStatus(@PathVariable UUID id,
                                                    @Valid @RequestBody ChangeStatusRequest status) {
        return ResponseEntity.ok(jobService.changeStatus(id, status.getStatus()));
    }



}
