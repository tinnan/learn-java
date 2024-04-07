package com.example.demo.batch.tut1.service;

import com.example.demo.batch.websocket.service.JobExecutionEventEmitter;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@AllArgsConstructor
public class JobNotificationListener implements JobExecutionListener {
    private final JobExecutionEventEmitter jobExecutionEventEmitter;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobExecutionEventEmitter.publish(jobExecution.getStartTime(), "Job start", jobExecution.getStatus()
                .toString());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        jobExecutionEventEmitter.publish(jobExecution.getEndTime(), "Job end", jobExecution.getStatus()
                .toString());
    }
}
