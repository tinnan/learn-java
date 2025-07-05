package com.github.tinnan.jobrunner.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.batch.core.BatchStatus;

@Getter
@Builder
public class BatchJob {

    private Long jobInstanceId;
    private String jobName;
    @JsonFormat(shape = Shape.STRING)
    private LocalDateTime startTime;
    @JsonFormat(shape = Shape.STRING)
    private LocalDateTime endTime;
    private BatchStatus status;
    private String exitCode;
    private String exitMessage;
}
