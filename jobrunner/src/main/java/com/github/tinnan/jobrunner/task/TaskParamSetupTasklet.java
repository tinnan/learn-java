package com.github.tinnan.jobrunner.task;

import com.github.tinnan.jobrunner.constants.JobStepAction;
import com.github.tinnan.jobrunner.entity.StartJobParam.Step;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class TaskParamSetupTasklet extends AbstractTasklet {

    private final Map<String, Object> params;

    public TaskParamSetupTasklet(Map<String, Object> params) {
        super(null);
        this.params = params;
    }

    @Override
    protected void validate(Step jobParamStep) {
        // Do nothing.
    }

    @Override
    protected JobStepAction associatedWithAction() {
        return null;
    }

    @Override
    public List<String> produceContextParams() {
        return params.keySet().stream().toList();
    }

    @Override
    public RepeatStatus internalExecute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        params.forEach((key, value) -> {
            log.debug("Setup param {}: {}", key, value);
            contribution.getStepExecution().getExecutionContext().put(key, value);
        });
        return RepeatStatus.FINISHED;
    }
}
