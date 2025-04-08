package com.jobscheduler.controller;

import com.jobscheduler.model.JobEntity;
import com.jobscheduler.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<String> scheduleJob(@RequestBody JobEntity job) {
        jobService.scheduleJob(job);
        return ResponseEntity.ok("Job Scheduled");
    }

    @GetMapping
    public List<JobEntity> getAllJobs() {
        return jobService.getAllJobs();
    }
}