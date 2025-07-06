package com.github.tinnan.jobrunner.task;

import com.github.tinnan.jobrunner.constants.JobStepAction;
import com.github.tinnan.jobrunner.entity.JobParam.Step;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class DevBuildDeployTasklet extends AbstractTasklet {

    public DevBuildDeployTasklet(Step jobParamStep) {
        super(jobParamStep);
    }

    @Override
    protected JobStepAction associatedWithAction() {
        return JobStepAction.DEV_BUILD_DEPLOY;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Long id = contribution.getStepExecution().getId();
        String stepName = contribution.getStepExecution().getStepName();
        log.info("{} - Step execution ID {}, Step name {}", associatedWithAction(), id, stepName);
        return RepeatStatus.FINISHED;
    }
}
