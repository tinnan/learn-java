package com.github.tinnan.jobrunner.service.impl;

import com.github.tinnan.jobrunner.entity.BatchJobInstance;
import com.github.tinnan.jobrunner.entity.BatchStepExecution;
import com.github.tinnan.jobrunner.entity.JobParam;
import com.github.tinnan.jobrunner.exception.JobParameterNotFoundException;
import com.github.tinnan.jobrunner.mapper.BatchJobMapper;
import com.github.tinnan.jobrunner.model.BatchJob;
import com.github.tinnan.jobrunner.model.BatchJobDetail;
import com.github.tinnan.jobrunner.model.JobStartResult;
import com.github.tinnan.jobrunner.repository.BatchJobInstanceRepository;
import com.github.tinnan.jobrunner.repository.BatchStepExecutionRepository;
import com.github.tinnan.jobrunner.service.JobBuilderService;
import com.github.tinnan.jobrunner.service.JobService;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
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
    public JobStartResult start(JobParam jobParam) {
        JobParameters params = getJobParameters();
        return null;
    }

    @Override
    public JobStartResult retry(long jobInstanceId) throws Exception {
        JobParameters params = getJobParameters(jobInstanceId);
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
        return BatchJobMapper.INSTANCE.mapToBatchJob(batchJobInstances);
    }

    @Override
    public BatchJobDetail fetchJobDetail(Long jobInstanceId) {
        List<BatchStepExecution> batchStepExecutions = batchStepExecutionRepository.findStepsByJobInstanceId(
            jobInstanceId);
        return BatchJobMapper.INSTANCE.mapToBatchJobDetail(batchStepExecutions);
    }

    private JobParameters getJobParameters() {
        return new JobParametersBuilder()
            .addString(JOB_PARAM_RUN_ID, UUID.randomUUID().toString())
            .toJobParameters();
    }

    private JobParameters getJobParameters(long jobInstanceId) {
        Optional<BatchJobInstance> batchJobInstance = batchJobInstanceRepository.findById(jobInstanceId);
        if (batchJobInstance.isEmpty()) {
            throw new JobParameterNotFoundException(JOB_PARAM_RUN_ID);
        }

        String jobParamRunId = BatchJobMapper.INSTANCE.getJobParamRunId(batchJobInstance.get());
        if (StringUtils.isBlank(jobParamRunId)) {
            throw new JobParameterNotFoundException(JOB_PARAM_RUN_ID);
        }
        return new JobParametersBuilder()
            .addString(JOB_PARAM_RUN_ID, jobParamRunId)
            .toJobParameters();
    }
}
