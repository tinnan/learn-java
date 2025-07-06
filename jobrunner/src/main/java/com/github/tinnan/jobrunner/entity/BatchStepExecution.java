package com.github.tinnan.jobrunner.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = BatchStepExecution.TABLE_NAME)
@Entity
public class BatchStepExecution {

    public static final String TABLE_NAME = "BATCH_STEP_EXECUTION";

    public static final String COLUMN_STEP_EXECUTION_ID = "STEP_EXECUTION_ID";
    public static final String COLUMN_VERSION = "VERSION";
    public static final String COLUMN_STEP_NAME = "STEP_NAME";
    public static final String COLUMN_JOB_EXECUTION_ID = "JOB_EXECUTION_ID";
    public static final String COLUMN_CREATE_TIME = "CREATE_TIME";
    public static final String COLUMN_START_TIME = "START_TIME";
    public static final String COLUMN_END_TIME = "END_TIME";
    public static final String COLUMN_STATUS = "STATUS";
    public static final String COLUMN_COMMIT_COUNT = "COMMIT_COUNT";
    public static final String COLUMN_READ_COUNT = "READ_COUNT";
    public static final String COLUMN_FILTER_COUNT = "FILTER_COUNT";
    public static final String COLUMN_WRITE_COUNT = "WRITE_COUNT";
    public static final String COLUMN_READ_SKIP_COUNT = "READ_SKIP_COUNT";
    public static final String COLUMN_WRITE_SKIP_COUNT = "WRITE_SKIP_COUNT";
    public static final String COLUMN_PROCESS_SKIP_COUNT = "PROCESS_SKIP_COUNT";
    public static final String COLUMN_ROLLBACK_COUNT = "ROLLBACK_COUNT";
    public static final String COLUMN_EXIT_CODE = "EXIT_CODE";
    public static final String COLUMN_EXIT_MESSAGE = "EXIT_MESSAGE";
    public static final String COLUMN_LAST_UPDATED = "LAST_UPDATED";

    @Id
    @Column(name = COLUMN_STEP_EXECUTION_ID)
    private Long stepExecutionId;

    @Column(name = COLUMN_VERSION)
    private Long version;

    @Column(name = COLUMN_STEP_NAME)
    private String stepName;

    @Column(name = COLUMN_CREATE_TIME)
    private LocalDateTime createTime;

    @Column(name = COLUMN_START_TIME)
    private LocalDateTime startTime;

    @Column(name = COLUMN_END_TIME)
    private LocalDateTime endTime;

    @Column(name = COLUMN_STATUS)
    private String status;

    @Column(name = COLUMN_COMMIT_COUNT)
    private Long commitCount;

    @Column(name = COLUMN_READ_COUNT)
    private Long readCount;

    @Column(name = COLUMN_FILTER_COUNT)
    private Long filterCount;

    @Column(name = COLUMN_WRITE_COUNT)
    private Long writeCount;

    @Column(name = COLUMN_READ_SKIP_COUNT)
    private Long readSkipCount;

    @Column(name = COLUMN_WRITE_SKIP_COUNT)
    private Long writeSkipCount;

    @Column(name = COLUMN_PROCESS_SKIP_COUNT)
    private Long processSkipCount;

    @Column(name = COLUMN_ROLLBACK_COUNT)
    private Long rollbackCount;

    @Column(name = COLUMN_EXIT_CODE)
    private String exitCode;

    @Column(name = COLUMN_EXIT_MESSAGE)
    private String exitMessage;

    @Column(name = COLUMN_LAST_UPDATED)
    private LocalDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = COLUMN_JOB_EXECUTION_ID,
        referencedColumnName = BatchJobExecution.COLUMN_JOB_EXECUTION_ID)
    private BatchJobExecution batchJobExecution;

    @OneToOne
    @JoinColumn(name = COLUMN_STEP_EXECUTION_ID,
        referencedColumnName = BatchStepExecutionContext.COLUMN_STEP_EXECUTION_ID)
    private BatchStepExecutionContext batchStepExecutionContext;

    @OneToOne
    @JoinColumn(name = COLUMN_STEP_EXECUTION_ID,
        referencedColumnName = BatchStepExecutionAdditionalData.COLUMN_STEP_EXECUTION_ID)
    private BatchStepExecutionAdditionalData batchStepExecutionAdditionalData;
}
