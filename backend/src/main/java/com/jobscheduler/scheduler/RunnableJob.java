package com.jobscheduler.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RunnableJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jarFileName = context.getMergedJobDataMap().getString("jarFileName");
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", "/minio/jars/" + jarFileName);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            throw new JobExecutionException("Failed to execute jar: " + jarFileName, e);
        }
    }
}