package com.jobscheduler.scheduler;

import com.jobscheduler.model.JobEntity;
import org.quartz.*;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JobScheduler {

    private final SchedulerFactoryBean schedulerFactoryBean;

    public JobScheduler(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    public void schedule(JobEntity job) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder.newJob(RunnableJob.class)
                    .withIdentity("job" + job.getId(), "group1")
                    .usingJobData("jarFileName", job.getJarFileName())
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger" + job.getId(), "group1")
                    .startAt(Date.from(job.getScheduledTime().toInstant()))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleRecurring(JobEntity job) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder.newJob(RunnableJob.class)
                    .withIdentity("recurringJob" + job.getId(), "group1")
                    .usingJobData("jarFileName", job.getJarFileName())
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("recurringTrigger" + job.getId(), "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule recurring job", e);
        }
    }

//    public void runJobImmediately(JobEntity job) {
//        try {
//            Scheduler scheduler = schedulerFactoryBean.getScheduler();
//            JobDetail jobDetail = JobBuilder.newJob(RunnableJob.class)
//                    .withIdentity("immediateJob" + job.getId(), "group1")
//                    .usingJobData("jarFileName", job.getJarFileName())
//                    .build();
//
//            scheduler.triggerJob(jobDetail.getKey(), jobDetail.getJobDataMap());
//        } catch (SchedulerException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void runJobImmediately(JobEntity job) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder.newJob(RunnableJob.class)
                    .withIdentity("immediateJob" + job.getId(), "group1")
                    .usingJobData("jarFileName", job.getJarFileName())
                    .storeDurably() // Important: allows adding job without trigger
                    .build();

            scheduler.addJob(jobDetail, true);

            scheduler.triggerJob(jobDetail.getKey(), jobDetail.getJobDataMap());

        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }


}