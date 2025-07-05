package com.github.tinnan.jobrunner.service;

import com.github.tinnan.jobrunner.model.BatchJob;
import com.github.tinnan.jobrunner.model.JobStartResult;
import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.batch.core.JobExecution;

public interface JobService {

    JobStartResult start(@Nullable Long jobInstanceId) throws Exception;

    List<BatchJob> fetchJobs(@Nullable Long jobInstanceIdOffset, @Nullable Long count);

    List<JobExecution> fetchJobStatus(Long jobId);
}
