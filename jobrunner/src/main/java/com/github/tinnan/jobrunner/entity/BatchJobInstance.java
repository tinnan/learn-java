package com.github.tinnan.jobrunner.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = BatchJobInstance.TABLE_NAME)
@Entity
public class BatchJobInstance {

    public static final String TABLE_NAME = "BATCH_JOB_INSTANCE";

    public static final String COLUMN_JOB_INSTANCE_ID = "JOB_INSTANCE_ID";
    public static final String COLUMN_VERSION = "VERSION";
    public static final String COLUMN_JOB_NAME = "JOB_NAME";
    public static final String COLUMN_JOB_KEY = "JOB_KEY";

    @Id
    @Column(name = COLUMN_JOB_INSTANCE_ID)
    private Long jobInstanceId;

    @Column(name = COLUMN_VERSION)
    private Long version;

    @Column(name = COLUMN_JOB_NAME)
    private String jobName;

    @Column(name = COLUMN_JOB_KEY)
    private String jobKey;

    @OneToMany
    @JoinColumn(name = COLUMN_JOB_INSTANCE_ID,
        referencedColumnName = BatchJobExecution.COLUMN_JOB_INSTANCE_ID)
    private List<BatchJobExecution> batchJobExecution;
}
