package com.github.tinnan.jobrunner.service.impl;

import com.github.tinnan.jobrunner.config.JobTaskDecorator;
import com.github.tinnan.jobrunner.constants.ExitStatus;
import com.github.tinnan.jobrunner.entity.BatchStepExecutionAdditionalData;
import com.github.tinnan.jobrunner.entity.StartJobParam;
import com.github.tinnan.jobrunner.service.JobBuilderService;
import com.github.tinnan.jobrunner.service.JobManagementService;
import com.github.tinnan.jobrunner.service.TaskletFactory;
import com.github.tinnan.jobrunner.task.AbstractTasklet;
import com.github.tinnan.jobrunner.task.TaskParamSetupTasklet;
import com.github.tinnan.jobrunner.task.listener.StepExecutionPromotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    public Job build(String jobName, StartJobParam jobParam) {
        List<Flow> flows = new ArrayList<>();

        List<String> tasks = jobParam.getServices();
        AtomicInteger taskNumberCounter = new AtomicInteger(1);
        tasks.forEach((taskName) -> {
            Iterator<StartJobParam.Step> jobParamSteps = jobParam.getSteps().iterator();
            int taskNumber = taskNumberCounter.getAndIncrement();

            FlowBuilder<Flow> flowBuilder = new FlowBuilder<>(taskName);
            flowBuilder.start(createTaskParamSetupStep(taskNumber, taskName));
            AtomicInteger stepNumberCounter = new AtomicInteger(1);
            Step currentStep = createStep(taskNumber, taskName, stepNumberCounter.getAndIncrement(),
                jobParamSteps.next());
            flowBuilder.next(currentStep);
            while (true) {
                if (jobParamSteps.hasNext()) {
                    Step nextStep = createStep(taskNumber, taskName, stepNumberCounter.getAndIncrement(),
                        jobParamSteps.next());
                    flowBuilder
                        .on(ExitStatus.PAUSED).stopAndRestart(nextStep)
                        .from(currentStep).on(org.springframework.batch.core.ExitStatus.FAILED.getExitCode()).fail()
                        .from(currentStep).on(ExitStatus.ANY).to(nextStep);
                    currentStep = nextStep;
                } else {
                    flowBuilder
                        .from(currentStep).on(org.springframework.batch.core.ExitStatus.FAILED.getExitCode()).fail()
                        .from(currentStep).on(ExitStatus.ANY).end();
                    break;
                }
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

    private Step createTaskParamSetupStep(int taskNumber, String taskName) {
        int stepNumber = -1;
        String stepName = createStepName(taskNumber, stepNumber);
        TaskParamSetupTasklet tasklet = new TaskParamSetupTasklet(Map.of("taskName", taskName));
        return new StepBuilder(stepName, jobRepository)
            .tasklet((contribution, chunkContext) -> {
                saveStepAdditionalData(contribution.getStepExecution().getId(), stepNumber, taskNumber, taskName);
                return tasklet.execute(contribution, chunkContext);
            }, transactionManager)
            .listener(new StepExecutionPromotionListener(tasklet.produceContextParams()))
            .build();
    }

    private Step createStep(int taskNumber, String taskName, int stepNumber, StartJobParam.Step jobStepParam) {
        // !Do not use random value for stepName unless you want to rerun every step when retry a job.
        // !Make stepName unique in scope of same job instance ID (and other customized criteria) to unsure
        // !completed steps are skipped when retried.
        String stepName = createStepName(taskNumber, stepNumber);
        AbstractTasklet tasklet = taskletFactory.createTasklet(jobStepParam);
        TaskletStepBuilder stepBuilder = new StepBuilder(stepName, jobRepository)
            .tasklet((contribution, chunkContext) -> {
                saveStepAdditionalData(contribution.getStepExecution().getId(), stepNumber, taskNumber, taskName);
                return tasklet.execute(contribution, chunkContext);
            }, transactionManager);
        if (!tasklet.produceContextParams().isEmpty()) {
            stepBuilder.listener(new StepExecutionPromotionListener(tasklet.produceContextParams()));
        }
        if (tasklet.isThrottled()) {
            stepBuilder.taskExecutor(createTaskExecutor(1));
        }
        return stepBuilder.build();
    }

    private String createStepName(int taskNumber, int stepNumber) {
        return "task_" + taskNumber + "_step_" + stepNumber;
    }

    private void saveStepAdditionalData(long stepExecutionId, int stepNumber, int taskNumber, String taskName) {
        BatchStepExecutionAdditionalData additionalData = BatchStepExecutionAdditionalData.builder()
            .stepExecutionId(stepExecutionId)
            .stepNumber(stepNumber)
            .taskNumber(taskNumber)
            .taskName(taskName)
            .build();
        jobManagementService.save(additionalData);
    }

    private TaskExecutor createTaskExecutor(int maxConcurrent) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setTaskDecorator(new JobTaskDecorator());
        taskExecutor.setCorePoolSize(maxConcurrent);
        taskExecutor.setMaxPoolSize(maxConcurrent);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
