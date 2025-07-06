package com.github.tinnan.jobrunner.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.batch.core.BatchStatus;

@Getter
@Builder
public class BatchStep {

    private String stepName;
    private BatchStatus status;
    @JsonFormat(shape = Shape.STRING)
    private LocalDateTime startTime;
    @JsonFormat(shape = Shape.STRING)
    private LocalDateTime endTime;

}
