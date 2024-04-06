package com.example.demo.batch.tut1;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
public class JobCompletionNotificationListener implements JobExecutionListener {
    private final PeopleRepository peopleRepository;
    private final JobRepository jobRepository;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Verify the result.");

            peopleRepository.findAll()
                    .forEach(person -> log.info("Found <{}> in the database.", person));
            jobRepository.findJobExecutions(jobExecution.getJobInstance())
                    .forEach(jobExe -> jobExe.getStepExecutions()
                            .forEach(stepExe -> log.info("Found step execution " +
                                    "<{}>", stepExe)));
        }
    }
}
