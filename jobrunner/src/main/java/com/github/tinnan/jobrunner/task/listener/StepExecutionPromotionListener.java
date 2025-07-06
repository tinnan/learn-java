package com.github.tinnan.jobrunner.task.listener;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@RequiredArgsConstructor
public class StepExecutionPromotionListener implements StepExecutionListener {

    private final List<String> contextParamNames;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        contextParamNames.forEach(contextParamName -> stepExecution.getJobExecution().getExecutionContext()
            .put(contextParamName, stepExecution.getExecutionContext().get(contextParamName)));
        return stepExecution.getExitStatus();
    }
}
