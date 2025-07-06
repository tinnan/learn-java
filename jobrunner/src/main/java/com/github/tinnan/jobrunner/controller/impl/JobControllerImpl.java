package com.github.tinnan.jobrunner.controller.impl;

import com.github.tinnan.jobrunner.controller.JobController;
import com.github.tinnan.jobrunner.entity.JobParam;
import com.github.tinnan.jobrunner.model.BatchJob;
import com.github.tinnan.jobrunner.model.BatchJobDetail;
import com.github.tinnan.jobrunner.model.JobStartResult;
import com.github.tinnan.jobrunner.service.JobService;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JobControllerImpl implements JobController {

    private final JobService jobService;

    @SneakyThrows
    @Override
    public JobStartResult startJob(JobParam jobParam) {
        return jobService.start(jobParam);
    }

    @SneakyThrows
    @Override
    public JobStartResult retryJob(Long jobInstanceId) {
        return jobService.retry(jobInstanceId);
    }

    @Override
    public List<BatchJob> fetchJobs(@Nullable Long jobInstanceIdOffset, @Nullable Long count) {
        return jobService.fetchJobs(jobInstanceIdOffset, count);
    }

    @Override
    public BatchJobDetail fetchJob(Long jobInstanceId) {
        return jobService.fetchJobDetail(jobInstanceId);
    }
}
