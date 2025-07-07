package com.github.tinnan.jobrunner.task;

import com.github.tinnan.jobrunner.constants.JobStepAction;
import com.github.tinnan.jobrunner.entity.StartJobParam.Step;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class SitBuildTasklet extends AbstractTasklet {

    public SitBuildTasklet(Step jobParamStep) {
        super(jobParamStep);
    }

    @Override
    protected JobStepAction associatedWithAction() {
        return JobStepAction.SIT_BUILD;
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
        log.info("{} - Step execution ID {}, Step name {} - Built to #{}", associatedWithAction(), id, stepName, tag);
        return RepeatStatus.FINISHED;
    }
}
