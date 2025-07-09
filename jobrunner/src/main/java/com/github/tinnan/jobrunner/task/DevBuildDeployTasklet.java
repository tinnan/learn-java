package com.github.tinnan.jobrunner.task;

import com.github.tinnan.jobrunner.constants.JobStepAction;
import com.github.tinnan.jobrunner.constants.JobParameterName;
import com.github.tinnan.jobrunner.entity.StartJobParam.Step;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class DevBuildDeployTasklet extends AbstractTasklet {

    private final String env;

    public DevBuildDeployTasklet(Step jobParamStep) {
        super(jobParamStep);
        this.env = jobParamStep.getParameterValue(JobParameterName.ENV);
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
        String stepName = contribution.getStepExecution().getStepName();
//        if ("task_1_step_2".equals(stepName)) {
//            contribution.setExitStatus(new ExitStatus(com.github.tinnan.jobrunner.constants.ExitStatus.PAUSED));
//            return RepeatStatus.FINISHED;
//        }
//        if ("task_2_step_2".equals(stepName)) {
//            throw new Exception("Fake error");
//        }
        String tag = String.valueOf((int) (Math.random() * 1000000));
        contribution.getStepExecution().getExecutionContext().put("tag", tag);
        Long id = contribution.getStepExecution().getId();
        log.info("{} - Step execution ID {}, Step name {} - Deploy to {} with result tag #{}", associatedWithAction(),
            id, stepName, env, tag);
        return RepeatStatus.FINISHED;
    }
}
