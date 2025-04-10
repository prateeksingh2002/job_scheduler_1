package com.jobscheduler.service;

import com.jobscheduler.model.JobEntity;
import com.jobscheduler.model.JobType;
import com.jobscheduler.repository.JobRepository;
import com.jobscheduler.scheduler.JobScheduler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final JobScheduler jobScheduler;

    public JobService(JobRepository jobRepository, JobScheduler jobScheduler) {
        this.jobRepository = jobRepository;
        this.jobScheduler = jobScheduler;
    }

    public void scheduleJob(JobEntity job) {
        jobRepository.save(job);
        if (job.getJobType() == JobType.IMMEDIATE) {
            jobScheduler.runJobImmediately(job);
//            try {
//                jobScheduler.runJobImmediately(job.getJobName(), job.getJarFileName());
//            } catch (Exception e) {
//                e.printStackTrace(); // or use a logger
//                throw new RuntimeException("Job execution failed", e); // optional
//            }
        } else if (job.getJobType() == JobType.RECURRING) {
            jobScheduler.scheduleRecurring(job);
        } else {
            jobScheduler.schedule(job);
        }
    }

    public List<JobEntity> getAllJobs() {
        return jobRepository.findAll();
    }
}