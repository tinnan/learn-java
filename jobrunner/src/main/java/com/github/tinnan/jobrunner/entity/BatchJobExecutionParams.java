package com.github.tinnan.jobrunner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = BatchJobExecutionParams.TABLE_NAME)
@Entity
public class BatchJobExecutionParams {

    public static final String TABLE_NAME = "BATCH_JOB_EXECUTION_PARAMS";

    public static final String COLUMN_JOB_EXECUTION_ID = "JOB_EXECUTION_ID";
    public static final String COLUMN_PARAMETER_NAME = "PARAMETER_NAME";
    public static final String COLUMN_PARAMETER_TYPE = "PARAMETER_TYPE";
    public static final String COLUMN_PARAMETER_VALUE = "PARAMETER_VALUE";
    public static final String COLUMN_IDENTIFYING = "IDENTIFYING";

    @Id
    @Column(name = COLUMN_JOB_EXECUTION_ID)
    private Long jobExecutionId;

    @Column(name = COLUMN_PARAMETER_NAME)
    private String parameterName;

    @Column(name = COLUMN_PARAMETER_TYPE)
    private String parameterType;

    @Column(name = COLUMN_PARAMETER_VALUE)
    private String parameterValue;

    @Column(name = COLUMN_IDENTIFYING)
    private String identifying;
}
