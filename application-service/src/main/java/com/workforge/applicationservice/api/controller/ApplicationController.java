package com.workforge.applicationservice.api.controller;

import com.workforge.applicationservice.api.dto.request.ChangeStatusRequest;
import com.workforge.applicationservice.api.dto.request.CreateApplicationRequest;
import com.workforge.applicationservice.api.dto.response.ApplicationResponse;
import com.workforge.applicationservice.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/application")
@AllArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(@Valid @RequestBody CreateApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createApplication(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplication(@PathVariable UUID id) {
        return ResponseEntity.ok(applicationService.getApplication(id));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationByJobId(@PathVariable UUID jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJobId(jobId));
    }

    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationByApplicantId(@PathVariable UUID applicantId) {
        return ResponseEntity.ok(applicationService.getApplicationsByApplicantId(applicantId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> changeStatus(@PathVariable UUID id, @Valid @RequestBody ChangeStatusRequest request) {
        return ResponseEntity.ok(applicationService.changeStatus(id, request));
    }
}
