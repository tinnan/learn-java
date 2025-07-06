package com.github.tinnan.jobrunner.service;

import com.github.tinnan.jobrunner.entity.JobParam;
import org.springframework.batch.core.step.tasklet.Tasklet;

public interface TaskletFactory {

    Tasklet createTasklet(JobParam.Step jobParamStep);
}
