package com.github.tinnan.jobrunner.task;

import com.github.tinnan.jobrunner.constants.JobStepAction;
import com.github.tinnan.jobrunner.constants.ParameterName;
import com.github.tinnan.jobrunner.entity.JobParam.Step;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class DevBuildDeployTasklet extends AbstractTasklet {

    private final String env;

    public DevBuildDeployTasklet(Step jobParamStep) {
        super(jobParamStep);
        this.env = jobParamStep.getParameterValue(ParameterName.ENV);
    }

    @Override
    protected JobStepAction associatedWithAction() {
        return JobStepAction.DEV_BUILD_DEPLOY;
    }

    @Override
    public List<String> produceContextParams() {
        return List.of("tag");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String tag = String.valueOf((int) (Math.random() * 1000000));
        contribution.getStepExecution().getExecutionContext().put("tag", tag);
        Long id = contribution.getStepExecution().getId();
        String stepName = contribution.getStepExecution().getStepName();
        log.info("{} - Step execution ID {}, Step name {} - Deploy to {} with result tag #{}", associatedWithAction(),
            id, stepName, env, tag);
        return RepeatStatus.FINISHED;
    }
}
