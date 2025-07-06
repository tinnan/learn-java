package com.github.tinnan.jobrunner.service;

import com.github.tinnan.jobrunner.model.event.BatchStepExecutionAdditionalDataEvent;

public interface JobManagementService {

    void listen(BatchStepExecutionAdditionalDataEvent event);
}
