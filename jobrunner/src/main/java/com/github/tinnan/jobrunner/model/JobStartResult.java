package com.github.tinnan.jobrunner.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobStartResult {

    private Long jobInstanceId;
    private String jobName;
}
