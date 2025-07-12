package com.github.tinnan.jobrunner.task;

import com.github.tinnan.jobrunner.constants.JobStepAction;
import com.github.tinnan.jobrunner.constants.JobParameterName;
import com.github.tinnan.jobrunner.entity.StartJobParam.Step;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class SitDeployDevTasklet extends AbstractTasklet {

    private final String env;

    public SitDeployDevTasklet(Step jobParamStep) {
        super(jobParamStep);
        this.env = jobParamStep.getParameterValue(JobParameterName.ENV);
    }

    @Override
    protected JobStepAction associatedWithAction() {
        return JobStepAction.SIT_DEPLOY_DEV;
    }

    @Override
    public RepeatStatus internalExecute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String tag = validateRequiredAndGetContextParam(contribution, "tag", String.class);
        Long id = contribution.getStepExecution().getId();
        String stepName = contribution.getStepExecution().getStepName();
        log.info("{} - Step execution ID {}, Step name {} - Deploy to {} using tag #{}", associatedWithAction(), id,
            stepName, env, tag);
        return RepeatStatus.FINISHED;
    }
}
