//package com.jobscheduler.scheduler;
//
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//
//public class RunnableJob implements Job {
//    @Override
//    public void execute(JobExecutionContext context) throws JobExecutionException {
//        String jarFileName = context.getMergedJobDataMap().getString("jarFileName");
//        try {
//            ProcessBuilder pb = new ProcessBuilder("java", "-jar", "/minio/jars/" + jarFileName);
//            pb.inheritIO();
//            Process process = pb.start();
//            process.waitFor();
//        } catch (Exception e) {
//            throw new JobExecutionException("Failed to execute jar: " + jarFileName, e);
//        }
//    }
//}

package com.jobscheduler.scheduler;

import io.minio.MinioClient;
import io.minio.GetObjectArgs;
import org.quartz.*;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RunnableJob implements Job {

    private static final String BUCKET_NAME = "jars";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jarFileName = context.getMergedJobDataMap().getString("jarFileName");

        try {
            // Init MinIO client
            MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://minio:9000") // Use service name "minio" from docker-compose if in same network
                    .credentials("minioadmin", "minioadmin")
                    .build();

            // Create tmp directory if not exists
            Files.createDirectories(Paths.get("/tmp/jars"));

            // Download JAR from MinIO to /tmp/jars
            String localPath = "/tmp/jars/" + jarFileName;
            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(jarFileName)
                            .build());
                 FileOutputStream outputStream = new FileOutputStream(localPath)) {
                inputStream.transferTo(outputStream);
            }

            // Execute the JAR
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", localPath);
            processBuilder.inheritIO(); // For logging to container console
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println("Job completed with exit code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
            throw new JobExecutionException("Failed to download or execute jar: " + jarFileName, e);
        }
    }
}
