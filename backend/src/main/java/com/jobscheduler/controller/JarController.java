package com.jobscheduler.controller;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/jars")
public class JarController {

    private final MinioClient minioClient;

    @Value("${minio.bucket:jars}")
    private String bucket;

    public JarController(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @GetMapping
    public ResponseEntity<List<String>> listJars() throws Exception {
        List<String> jarFiles = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucket).build()
        );

        for (Result<Item> result : results) {
            jarFiles.add(result.get().objectName());
        }

        return ResponseEntity.ok(jarFiles);
    }
}
