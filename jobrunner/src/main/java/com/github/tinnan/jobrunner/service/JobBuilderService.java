package com.github.tinnan.jobrunner.service;

import org.springframework.batch.core.Job;

public interface JobBuilderService {

    Job build(String jobName);
}
