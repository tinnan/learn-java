package com.github.tinnan.jobrunner.service.impl;

import com.github.tinnan.jobrunner.entity.StartJobParam.Step;
import com.github.tinnan.jobrunner.service.TaskletFactory;
import com.github.tinnan.jobrunner.task.AbstractTasklet;
import com.github.tinnan.jobrunner.task.DeployReleaseTasklet;
import com.github.tinnan.jobrunner.task.DevBuildDeployTasklet;
import com.github.tinnan.jobrunner.task.MergeTasklet;
import com.github.tinnan.jobrunner.task.SitBuildTasklet;
import com.github.tinnan.jobrunner.task.SitDeployDevTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskletFactoryImpl implements TaskletFactory {

    @Override
    public AbstractTasklet createTasklet(Step jobParamStep) {
        return switch (jobParamStep.getAction()) {
            case MERGE -> new MergeTasklet(jobParamStep);
            case DEV_BUILD_DEPLOY -> new DevBuildDeployTasklet(jobParamStep);
            case SIT_BUILD -> new SitBuildTasklet(jobParamStep);
            case SIT_DEPLOY_DEV -> new SitDeployDevTasklet(jobParamStep);
            case DEPLOY_RELEASE -> new DeployReleaseTasklet(jobParamStep);
        };
    }
}
