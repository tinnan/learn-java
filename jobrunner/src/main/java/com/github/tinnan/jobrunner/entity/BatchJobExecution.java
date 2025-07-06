package com.github.tinnan.jobrunner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = BatchJobExecution.TABLE_NAME)
@Entity
public class BatchJobExecution {

    public static final String TABLE_NAME = "BATCH_JOB_EXECUTION";

    public static final String COLUMN_JOB_EXECUTION_ID = "JOB_EXECUTION_ID";
    public static final String COLUMN_VERSION = "VERSION";
    public static final String COLUMN_JOB_INSTANCE_ID = "JOB_INSTANCE_ID";
    public static final String COLUMN_CREATE_TIME = "CREATE_TIME";
    public static final String COLUMN_START_TIME = "START_TIME";
    public static final String COLUMN_END_TIME = "END_TIME";
    public static final String COLUMN_STATUS = "STATUS";
    public static final String COLUMN_EXIT_CODE = "EXIT_CODE";
    public static final String COLUMN_EXIT_MESSAGE = "EXIT_MESSAGE";
    public static final String COLUMN_LAST_UPDATED = "LAST_UPDATED";

    @Id
    @Column(name = COLUMN_JOB_EXECUTION_ID)
    private Long jobExecutionId;

    @Column(name = COLUMN_VERSION)
    private Long version;

    @Column(name = COLUMN_JOB_INSTANCE_ID)
    private Long jobInstanceId;

    @Column(name = COLUMN_CREATE_TIME)
    private LocalDateTime createTime;

    @Column(name = COLUMN_START_TIME)
    private LocalDateTime startTime;

    @Column(name = COLUMN_END_TIME)
    private LocalDateTime endTime;

    @Column(name = COLUMN_STATUS)
    private String status;

    @Column(name = COLUMN_EXIT_CODE)
    private String exitCode;

    @Column(name = COLUMN_EXIT_MESSAGE)
    private String exitMessage;

    @Column(name = COLUMN_LAST_UPDATED)
    private LocalDateTime lastUpdated;

    @OneToMany
    @JoinColumn(name = COLUMN_JOB_EXECUTION_ID,
        referencedColumnName = BatchJobExecutionParams.COLUMN_JOB_EXECUTION_ID)
    private List<BatchJobExecutionParams> batchJobExecutionParams;
}
