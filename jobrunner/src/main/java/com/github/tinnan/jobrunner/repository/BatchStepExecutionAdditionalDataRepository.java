package com.github.tinnan.jobrunner.repository;

import com.github.tinnan.jobrunner.entity.BatchStepExecutionAdditionalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchStepExecutionAdditionalDataRepository extends
    JpaRepository<BatchStepExecutionAdditionalData, Long> {

}
