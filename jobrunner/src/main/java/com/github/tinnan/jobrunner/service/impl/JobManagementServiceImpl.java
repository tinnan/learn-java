package com.github.tinnan.jobrunner.service.impl;

import com.github.tinnan.jobrunner.entity.BatchStepExecutionAdditionalData;
import com.github.tinnan.jobrunner.repository.BatchStepExecutionAdditionalDataRepository;
import com.github.tinnan.jobrunner.service.JobManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobManagementServiceImpl implements JobManagementService {

    private final BatchStepExecutionAdditionalDataRepository additionalDataRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void save(BatchStepExecutionAdditionalData data) {
        additionalDataRepository.save(data);
    }
}
