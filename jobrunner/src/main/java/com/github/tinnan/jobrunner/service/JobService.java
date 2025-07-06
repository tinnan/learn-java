package com.github.tinnan.jobrunner.service;

import com.github.tinnan.jobrunner.entity.JobParam;
import com.github.tinnan.jobrunner.model.BatchJob;
import com.github.tinnan.jobrunner.model.BatchJobDetail;
import com.github.tinnan.jobrunner.model.JobStartResult;
import jakarta.annotation.Nullable;
import java.util.List;

public interface JobService {

    JobStartResult start(JobParam jobParam);

    JobStartResult retry(long jobInstanceId) throws Exception;

    List<BatchJob> fetchJobs(@Nullable Long jobInstanceIdOffset, @Nullable Long count);

    BatchJobDetail fetchJobDetail(Long jobInstanceId);
}
