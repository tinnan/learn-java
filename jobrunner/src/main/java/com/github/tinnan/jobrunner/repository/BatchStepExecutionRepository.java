package com.github.tinnan.jobrunner.repository;

import com.github.tinnan.jobrunner.entity.BatchStepExecution;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchStepExecutionRepository extends JpaRepository<BatchStepExecution, Long> {

    @Query("""
        SELECT bse
        FROM BatchStepExecution bse
        WHERE bse.batchJobExecution.batchJobInstance.jobInstanceId = :JobInstanceId
            AND bse.batchStepExecutionAdditionalData.stepNumber > 0
        ORDER BY
            bse.batchJobExecution.batchJobInstance.jobInstanceId ASC,
            bse.batchJobExecution.jobExecutionId ASC,
            bse.stepExecutionId ASC
        """)
    List<BatchStepExecution> findStepsByJobInstanceId(Long JobInstanceId);
}
