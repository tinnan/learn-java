package com.github.tinnan.jobrunner.service;

import com.github.tinnan.jobrunner.entity.JobParam;
import org.springframework.batch.core.Job;

public interface JobBuilderService {

    Job build(String jobName, JobParam jobParam);
}
