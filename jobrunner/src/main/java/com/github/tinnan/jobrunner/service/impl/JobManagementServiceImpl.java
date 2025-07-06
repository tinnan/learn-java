package com.github.tinnan.jobrunner.service.impl;

import com.github.tinnan.jobrunner.entity.BatchStepExecutionAdditionalData;
import com.github.tinnan.jobrunner.model.event.BatchStepExecutionAdditionalDataEvent;
import com.github.tinnan.jobrunner.repository.BatchStepExecutionAdditionalDataRepository;
import com.github.tinnan.jobrunner.service.JobManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobManagementServiceImpl implements JobManagementService {

    private final BatchStepExecutionAdditionalDataRepository additionalDataRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void listen(BatchStepExecutionAdditionalDataEvent event) {
        BatchStepExecutionAdditionalData data = event.getData();
        log.debug("Listened to {} - stepExecutionId={}", event.getClass().getName(), data.getStepExecutionId());
        additionalDataRepository.save(data);
    }
}
