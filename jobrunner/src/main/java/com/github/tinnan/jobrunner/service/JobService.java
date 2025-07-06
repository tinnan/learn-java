package com.github.tinnan.jobrunner.service;

import com.github.tinnan.jobrunner.model.BatchJob;
import com.github.tinnan.jobrunner.model.BatchTask;
import com.github.tinnan.jobrunner.model.JobStartResult;
import jakarta.annotation.Nullable;
import java.util.List;

public interface JobService {

    JobStartResult start(@Nullable Long jobInstanceId) throws Exception;

    List<BatchJob> fetchJobs(@Nullable Long jobInstanceIdOffset, @Nullable Long count);

    List<BatchTask> fetchJobTasks(Long jobInstanceId);
}
