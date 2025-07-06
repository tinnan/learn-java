package com.github.tinnan.jobrunner.service.impl;

import com.github.tinnan.jobrunner.entity.BatchStepExecutionAdditionalData;
import com.github.tinnan.jobrunner.entity.JobParam;
import com.github.tinnan.jobrunner.service.JobBuilderService;
import com.github.tinnan.jobrunner.service.JobManagementService;
import com.github.tinnan.jobrunner.service.TaskletFactory;
import com.github.tinnan.jobrunner.task.AbstractTasklet;
import com.github.tinnan.jobrunner.task.listener.StepExecutionPromotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.builder.TaskletStepBuilder;
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
    private final TaskletFactory taskletFactory;
    private final JobManagementService jobManagementService;

    @Override
    public Job build(String jobName, JobParam jobParam) {
        List<Flow> flows = new ArrayList<>();

        List<String> tasks = jobParam.getServices();
        List<JobParam.Step> jobParamSteps = jobParam.getSteps();
        AtomicInteger taskNumberCounter = new AtomicInteger(1);
        tasks.forEach((taskName) -> {
            int taskNumber = taskNumberCounter.getAndIncrement();
            FlowBuilder<Flow> flowBuilder = new FlowBuilder<>(taskName);
            AtomicInteger stepNumberCounter = new AtomicInteger(1);
            jobParamSteps.forEach((jobParamStep) -> flowBuilder.next(
                createStep(taskNumber, taskName, stepNumberCounter.getAndIncrement(), jobParamStep)));
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

    private Step createStep(int taskNumber, String taskName, int stepNumber, JobParam.Step jobStepParam) {
        // !Do not use random value for stepName unless you want to rerun every step when retry a job.
        // !Make stepName unique in scope of same job instance ID (and other customized criteria) to unsure
        // !completed steps are skipped when retried.
        String stepName = "task_" + taskNumber + "_step_" + stepNumber;
        AbstractTasklet tasklet = taskletFactory.createTasklet(jobStepParam);
        TaskletStepBuilder stepBuilder = new StepBuilder(stepName, jobRepository)
            .tasklet((contribution, chunkContext) -> {
                BatchStepExecutionAdditionalData additionalData = BatchStepExecutionAdditionalData.builder()
                    .stepExecutionId(contribution.getStepExecution().getId())
                    .stepNumber(stepNumber)
                    .taskNumber(taskNumber)
                    .taskName(taskName)
                    .build();
                jobManagementService.save(additionalData);
                return tasklet.execute(contribution, chunkContext);
            }, transactionManager);
        if (!tasklet.produceContextParams().isEmpty()) {
            stepBuilder.listener(new StepExecutionPromotionListener(tasklet.produceContextParams()));
        }
        return stepBuilder.build();
    }

    private TaskExecutor createTaskExecutor(int maxConcurrent) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(maxConcurrent);
        taskExecutor.setMaxPoolSize(maxConcurrent);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
