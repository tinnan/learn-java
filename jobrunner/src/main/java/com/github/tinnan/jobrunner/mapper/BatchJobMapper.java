package com.github.tinnan.jobrunner.mapper;

import static com.github.tinnan.jobrunner.service.impl.JobServiceImpl.JOB_PARAM_RUN_ID;

import com.github.tinnan.jobrunner.entity.BatchJobExecution;
import com.github.tinnan.jobrunner.entity.BatchJobExecutionParams;
import com.github.tinnan.jobrunner.entity.BatchJobInstance;
import com.github.tinnan.jobrunner.model.BatchJob;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchJobMapper {

    BatchJobMapper INSTANCE = Mappers.getMapper(BatchJobMapper.class);

    List<BatchJob> mapToBatchJobs(List<BatchJobInstance> source);

    default BatchJob mapToBatchJob(BatchJobInstance source) {
        BatchJobExecution batchJobExecution = getLatestBatchJobExecution(source);
        return mapToBatchJob(source, batchJobExecution);
    }

    @Mapping(target = "jobInstanceId", source = "batchJobInstance.jobInstanceId")
    BatchJob mapToBatchJob(BatchJobInstance batchJobInstance, BatchJobExecution batchJobExecution);

    default BatchJobExecution getLatestBatchJobExecution(BatchJobInstance source) {
        return Optional.ofNullable(source.getBatchJobExecution())
            .orElse(Collections.emptyList())
            .stream()
            .max(Comparator.comparingLong(BatchJobExecution::getJobExecutionId))
            .orElse(null);
    }

    default String getJobParamRunId(BatchJobInstance source) {
        BatchJobExecution latestBatchJobExecution = getLatestBatchJobExecution(source);
        return Optional.ofNullable(latestBatchJobExecution)
            .map(BatchJobExecution::getBatchJobExecutionParams)
            .orElse(Collections.emptyList())
            .stream().filter(param -> JOB_PARAM_RUN_ID.equals(param.getParameterName()))
            .findFirst()
            .map(BatchJobExecutionParams::getParameterValue)
            .orElse(null);
    }
}
