package com.github.tinnan.jobrunner.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BatchTask {

    private Integer taskNumber;
    private List<BatchStep> steps;
}
