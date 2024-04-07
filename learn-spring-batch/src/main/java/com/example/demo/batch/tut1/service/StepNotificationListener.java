package com.example.demo.batch.tut1.service;

import com.example.demo.batch.websocket.service.JobExecutionEventEmitter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@AllArgsConstructor
@Slf4j
public class StepNotificationListener implements StepExecutionListener {
    private final JobExecutionEventEmitter jobExecutionEventEmitter;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        jobExecutionEventEmitter.publish(stepExecution.getStartTime(), stepExecution.getStepName() + " - start",
                stepExecution.getStatus()
                        .toString());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        jobExecutionEventEmitter.publish(stepExecution.getLastUpdated(), stepExecution.getStepName() + " - end",
                stepExecution.getStatus()
                        .toString());
        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
