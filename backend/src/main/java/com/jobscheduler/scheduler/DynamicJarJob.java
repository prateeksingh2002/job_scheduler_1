package com.jobscheduler.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;

public class DynamicJarJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jarFileName = context.getMergedJobDataMap().getString("jarFileName");
        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket("jars")
                        .object(jarFileName)
                        .filename("jars/" + jarFileName)
                        .build()
        );

        try {
            String jarPath = "jars/" + jarFileName; // Change this to your actual mount/path
            File jarFile = new File(jarPath);

            if (!jarFile.exists()) {
                throw new RuntimeException("JAR file not found: " + jarPath);
            }

            URL[] urls = { jarFile.toURI().toURL() };
            try (URLClassLoader classLoader = new URLClassLoader(urls)) {

                // You might want to standardize on a known entry class/method
                Class<?> mainClass = classLoader.loadClass("com.jobscheduler.Application"); // Adjust accordingly
                Method mainMethod = mainClass.getMethod("main", String[].class);

                String[] args = new String[] {};
                mainMethod.invoke(null, (Object) args); // Cast required to avoid varargs warning

                System.out.println("Successfully executed: " + jarFileName);
            }
        } catch (Exception e) {
            throw new JobExecutionException("Failed to execute dynamic JAR: " + jarFileName, e);
        }
    }
}
