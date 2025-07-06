package com.github.tinnan.jobrunner.mapper;

import static com.github.tinnan.jobrunner.service.impl.JobServiceImpl.JOB_PARAM_RUN_ID;

import com.github.tinnan.jobrunner.entity.BatchJobExecution;
import com.github.tinnan.jobrunner.entity.BatchJobExecutionParams;
import com.github.tinnan.jobrunner.entity.BatchJobInstance;
import com.github.tinnan.jobrunner.entity.BatchStepExecution;
import com.github.tinnan.jobrunner.model.BatchJob;
import com.github.tinnan.jobrunner.model.BatchJobDetail;
import com.github.tinnan.jobrunner.model.BatchStep;
import com.github.tinnan.jobrunner.model.BatchTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchJobMapper {

    BatchJobMapper INSTANCE = Mappers.getMapper(BatchJobMapper.class);

    List<BatchJob> mapToBatchJob(List<BatchJobInstance> source);

    default BatchJob mapToBatchJob(BatchJobInstance source) {
        BatchJobExecution batchJobExecution = getLatestBatchJobExecution(source);
        return mapToBatchJob(source, batchJobExecution);
    }

    BatchJob mapToBatchJob(BatchJobInstance batchJobInstance, BatchJobExecution batchJobExecution);

    BatchJobDetail mapToBatchJobDetail(BatchJobInstance batchJobInstance, BatchJobExecution batchJobExecution,
        List<BatchTask> tasks);

    default BatchJobDetail mapToBatchJobDetail(List<BatchStepExecution> source) {
        BatchJobExecution batchJobExecution = Optional.ofNullable(source)
            .orElse(Collections.emptyList()).stream()
            .findFirst()
            .map(BatchStepExecution::getBatchJobExecution)
            .orElse(null);
        BatchJobInstance batchJobInstance = Optional.ofNullable(batchJobExecution)
            .map(BatchJobExecution::getBatchJobInstance)
            .orElse(null);

        List<BatchTask> tasks = new ArrayList<>();
        Optional.ofNullable(source)
            .orElse(Collections.emptyList())
            .stream()
            .collect(Collectors.groupingBy(
                step -> step.getBatchStepExecutionAdditionalData().getTaskNumber(),
                Collectors.collectingAndThen(
                    Collectors.toMap(
                        step -> step.getBatchStepExecutionAdditionalData().getStepNumber(),
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparingLong(BatchStepExecution::getStepExecutionId))
                    ),
                    map -> new ArrayList<>(map.values())
                )
            )).forEach((taskNumber, steps) -> tasks.add(mapToBatchTask(taskNumber, mapToBatchStep(steps))));

        return mapToBatchJobDetail(batchJobInstance, batchJobExecution, tasks);
    }

    BatchTask mapToBatchTask(Integer taskNumber, List<BatchStep> steps);

    List<BatchStep> mapToBatchStep(List<BatchStepExecution> source);

    @Mapping(target = "stepNumber", source = "batchStepExecutionAdditionalData.stepNumber")
    BatchStep mapToBatchStep(BatchStepExecution source);

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
