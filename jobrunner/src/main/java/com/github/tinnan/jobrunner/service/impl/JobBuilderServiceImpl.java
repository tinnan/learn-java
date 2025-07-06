package com.github.tinnan.jobrunner.service.impl;

import com.github.tinnan.jobrunner.entity.BatchStepExecutionAdditionalData;
import com.github.tinnan.jobrunner.model.event.BatchStepExecutionAdditionalDataEvent;
import com.github.tinnan.jobrunner.service.JobBuilderService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobBuilderServiceImpl implements JobBuilderService {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Job build(String jobName) {
        List<Flow> flows = new ArrayList<>();
        IntStream.range(1, 4).forEach(taskNumber -> {

            String taskName = "task-" + taskNumber;
            FlowBuilder<Flow> flowBuilder = new FlowBuilder<>(taskName);
            flowBuilder.start(createStep(taskNumber, taskName, 1));

            for (int i = 2; i < 4; i++) {
                flowBuilder.next(createStep(taskNumber, taskName, i));
            }

            flows.add(flowBuilder.build());
        });

        Flow flow = new FlowBuilder<Flow>("flow-" + jobName)
            .split(createTaskExecutor(2))
            .add(flows.toArray(new Flow[0]))
            .build();
        return new JobBuilder(jobName, jobRepository)
            .start(flow).build()
            .build();
    }

    private Step createStep(int taskNumber, String taskName, int stepNumber) {
        // !Do not use random value for stepName unless you want to rerun every step when retry a job.
        // !Make stepName unique in scope of same job instance ID (and other customized criteria) to unsure
        // !completed steps are skipped when retried.
        String stepName = taskName + "-step-" + stepNumber;
        return new StepBuilder(stepName, jobRepository)
            .tasklet((contribution, chunkContext) -> {
                BatchStepExecutionAdditionalData additionalData = BatchStepExecutionAdditionalData.builder()
                    .stepExecutionId(contribution.getStepExecution().getId())
                    .stepNumber(stepNumber)
                    .taskNumber(taskNumber)
                    .taskName(taskName)
                    .build();
                applicationEventPublisher.publishEvent(new BatchStepExecutionAdditionalDataEvent(this, additionalData));
                // Simulate failure for retry
                int rand = (int) (Math.random() * 100);
                if (rand % 2 == 0) {
                    throw new RuntimeException("Step " + stepName + " failed");
                }
                log.info("Executing {} - finished", stepName);
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    private TaskExecutor createTaskExecutor(int maxConcurrent) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(maxConcurrent);
        taskExecutor.setMaxPoolSize(maxConcurrent);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
