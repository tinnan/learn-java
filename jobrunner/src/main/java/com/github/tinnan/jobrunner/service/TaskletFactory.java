package com.github.tinnan.jobrunner.service;

import com.github.tinnan.jobrunner.entity.StartJobParam.Step;
import com.github.tinnan.jobrunner.task.AbstractTasklet;

public interface TaskletFactory {

    AbstractTasklet createTasklet(Step jobParamStep);
}
