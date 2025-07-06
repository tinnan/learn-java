package com.github.tinnan.jobrunner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = BatchStepExecutionAdditionalData.TABLE_NAME)
@Entity
public class BatchStepExecutionAdditionalData implements Serializable {

    public static final String TABLE_NAME = "BATCH_STEP_EXECUTION_ADDITIONAL_DATA";

    public static final String COLUMN_STEP_EXECUTION_ID = "STEP_EXECUTION_ID";
    public static final String COLUMN_STEP_NUMBER = "STEP_NUMBER";
    public static final String COLUMN_TASK_NAME = "TASK_NAME";
    @Serial
    private static final long serialVersionUID = -5071377997797079239L;

    @Id
    @Column(name = COLUMN_STEP_EXECUTION_ID)
    private Long stepExecutionId;

    @Column(name = COLUMN_STEP_NUMBER)
    private Long stepNumber;

    @Column(name = COLUMN_TASK_NAME)
    private String altTaskName;
}
