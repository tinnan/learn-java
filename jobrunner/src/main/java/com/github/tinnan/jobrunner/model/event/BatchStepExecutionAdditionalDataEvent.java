package com.github.tinnan.jobrunner.model.event;

import com.github.tinnan.jobrunner.entity.BatchStepExecutionAdditionalData;
import java.io.Serial;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BatchStepExecutionAdditionalDataEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = -4082713310099391779L;
    private final BatchStepExecutionAdditionalData data;

    public BatchStepExecutionAdditionalDataEvent(Object source, BatchStepExecutionAdditionalData data) {
        super(source);
        this.data = data;
    }
}
