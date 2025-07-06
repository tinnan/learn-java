package com.github.tinnan.jobrunner.entity;

import com.github.tinnan.jobrunner.entity.converter.ShortContextToExecutionContextConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.batch.item.ExecutionContext;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = BatchStepExecutionContext.TABLE_NAME)
@Entity
public class BatchStepExecutionContext {

    public static final String TABLE_NAME = "BATCH_STEP_EXECUTION_CONTEXT";

    public static final String COLUMN_STEP_EXECUTION_ID = "STEP_EXECUTION_ID";
    public static final String COLUMN_SHORT_CONTEXT = "SHORT_CONTEXT";
    public static final String COLUMN_SERIALIZED_CONTEXT = "SERIALIZED_CONTEXT";

    @Id
    @Column(name = COLUMN_STEP_EXECUTION_ID)
    private Long stepExecutionId;

    @Convert(converter = ShortContextToExecutionContextConverter.class)
    @Column(name = COLUMN_SHORT_CONTEXT)
    private ExecutionContext shortContext;

    @Column(name = COLUMN_SERIALIZED_CONTEXT)
    private String serializedContext;
}

