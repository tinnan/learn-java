package com.github.tinnan.jobrunner.service.impl;

import com.github.tinnan.jobrunner.entity.BatchJobInstance;
import com.github.tinnan.jobrunner.entity.BatchStepExecution;
import com.github.tinnan.jobrunner.mapper.BatchJobMapper;
import com.github.tinnan.jobrunner.model.BatchJob;
import com.github.tinnan.jobrunner.model.BatchTask;
import com.github.tinnan.jobrunner.model.JobStartResult;
import com.github.tinnan.jobrunner.repository.BatchJobInstanceRepository;
import com.github.tinnan.jobrunner.repository.BatchStepExecutionRepository;
import com.github.tinnan.jobrunner.service.JobBuilderService;
import com.github.tinnan.jobrunner.service.JobService;
import jakarta.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    public static final String JOB_NAME = "custom-job";
    public static final String JOB_PARAM_RUN_ID = "runId";
    private static final long JOB_PAGE_SIZE = 30;

    private final JobLauncher jobLauncher;
    private final JobBuilderService jobBuilderService;
    private final BatchJobInstanceRepository batchJobInstanceRepository;
    private final BatchStepExecutionRepository batchStepExecutionRepository;

    @Override
    public JobStartResult start(@Nullable Long jobInstanceId) throws Exception {
        String runId = getRunId(jobInstanceId);
        JobParameters params = new JobParametersBuilder()
            .addString(JOB_PARAM_RUN_ID, runId)
            .toJobParameters();
        Job job = jobBuilderService.build(JOB_NAME);
        JobExecution jobExecution = jobLauncher.run(job, params);
        return JobStartResult.builder()
            .jobInstanceId(jobExecution.getJobId())
            .jobName(job.getName())
            .build();
    }

    @Override
    public List<BatchJob> fetchJobs(@Nullable Long jobInstanceIdOffset, @Nullable Long count) {
        List<BatchJobInstance> batchJobInstances = batchJobInstanceRepository
            .findJobs(JOB_NAME, jobInstanceIdOffset, Optional.ofNullable(count).orElse(JOB_PAGE_SIZE));
        return BatchJobMapper.INSTANCE.mapToBatchJobs(batchJobInstances);
    }

    @Override
    public List<BatchTask> fetchJobTasks(Long jobInstanceId) {
        List<BatchStepExecution> batchStepExecutions = batchStepExecutionRepository.findStepsByJobInstanceId(
            jobInstanceId);
        return Collections.emptyList();
    }

    private String getRunId(@Nullable Long jobInstanceId) {
        if (jobInstanceId == null) {
            return UUID.randomUUID().toString();
        }
        Optional<BatchJobInstance> batchJobInstance = batchJobInstanceRepository.findById(jobInstanceId);
        if (batchJobInstance.isEmpty()) {
            return UUID.randomUUID().toString();
        }

        String jobParamRunId = BatchJobMapper.INSTANCE.getJobParamRunId(batchJobInstance.get());
        if (StringUtils.isBlank(jobParamRunId)) {
            return UUID.randomUUID().toString();
        }
        return jobParamRunId;
    }
}
