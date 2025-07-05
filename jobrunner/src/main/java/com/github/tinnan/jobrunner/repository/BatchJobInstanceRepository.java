package com.github.tinnan.jobrunner.repository;

import com.github.tinnan.jobrunner.entity.BatchJobInstance;
import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchJobInstanceRepository extends JpaRepository<BatchJobInstance, Long> {

    @Query("""
        SELECT bji FROM BATCH_JOB_INSTANCE bji
        WHERE bji.jobName = :jobName
            AND (:jobInstanceIdOffset IS NULL OR bji.jobInstanceId < :jobInstanceIdOffset)
        ORDER BY bji.jobInstanceId DESC
        LIMIT :limit
        """)
    List<BatchJobInstance> findJobs(String jobName, @Nullable Long jobInstanceIdOffset, Long limit);
}
