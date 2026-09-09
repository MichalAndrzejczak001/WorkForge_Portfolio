package com.workforge.jobservice.infrastructure.scheduling;

import com.workforge.jobservice.application.service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobExpiryScheduler {
    private final JobService jobService;

    @Scheduled(fixedRate = 60000)
    public void checkExpiredJobs() {
        jobService.expireOverdueJobs();
    }
}
