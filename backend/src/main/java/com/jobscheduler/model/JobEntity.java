package com.jobscheduler.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobName;
    private String jarFileName;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    private ZonedDateTime scheduledTime;
    private String cronExpression;
    private String timeZone;
    private String status;

    // Getters and Setters
}
