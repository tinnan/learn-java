package com.github.tinnan.jobrunner.service;

import com.github.tinnan.jobrunner.entity.BatchStepExecutionAdditionalData;

public interface JobManagementService {

    void save(BatchStepExecutionAdditionalData event);
}
