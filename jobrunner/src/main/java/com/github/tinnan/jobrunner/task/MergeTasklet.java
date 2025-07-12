package com.github.tinnan.jobrunner.task;

import com.github.tinnan.jobrunner.config.JobConfigHolder;
import com.github.tinnan.jobrunner.constants.JobParameterName;
import com.github.tinnan.jobrunner.constants.JobStepAction;
import com.github.tinnan.jobrunner.constants.TaskletThrottleGroup;
import com.github.tinnan.jobrunner.entity.StartJobParam.Step;
import com.github.tinnan.jobrunner.exception.JobParameterViolationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class MergeTasklet extends AbstractTasklet {

    private static final Pattern DEV_RELEASE_BRANCH_PATTERN = Pattern.compile("^(develop-|release/)r(\\d+)$");

    private final String fromBranch;
    private final String toBranch;

    public MergeTasklet(Step jobParamStep) {
        super(jobParamStep);
        this.fromBranch = jobParamStep.getParameterValue(JobParameterName.FROM_BRANCH);
        this.toBranch = jobParamStep.getParameterValue(JobParameterName.TO_BRANCH);
        validateMergeTarget();
    }

    @Override
    protected JobStepAction associatedWithAction() {
        return JobStepAction.MERGE;
    }

    @Override
    public TaskletThrottleGroup getThrottleLimit() {
        return TaskletThrottleGroup.JENKINS;
    }

    private void validateMergeTarget() {
        Matcher fromBranchMatcher = DEV_RELEASE_BRANCH_PATTERN.matcher(this.fromBranch);
        Matcher toBranchMatcher = DEV_RELEASE_BRANCH_PATTERN.matcher(this.toBranch);
        if (fromBranchMatcher.matches() && toBranchMatcher.matches()) {
            int fromReleaseNumber = Integer.parseInt(fromBranchMatcher.group(2));
            int toReleaseNumber = Integer.parseInt(toBranchMatcher.group(2));
            if (fromReleaseNumber > toReleaseNumber) {
                throw new JobParameterViolationException("Merging to previous releases are not allowed");
            }
        }
    }

    @Override
    public RepeatStatus internalExecute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String stepName = contribution.getStepExecution().getStepName();
//        if ("task_1_step_1".equals(stepName)) {
//            contribution.setExitStatus(new ExitStatus(com.github.tinnan.jobrunner.constants.ExitStatus.PAUSED));
//            return RepeatStatus.FINISHED;
//        }
//        if ("task_1_step_1".equals(stepName)) {
//            contribution.setExitStatus(new ExitStatus(ExitStatus.FAILED.getExitCode(), "Fake error"));
//            return RepeatStatus.FINISHED;
//        }
        log.info("{} - Job config value: {}", associatedWithAction(), JobConfigHolder.get());
        String taskName = contribution.getStepExecution().getJobExecution().getExecutionContext()
            .get("taskName", String.class);
        Long id = contribution.getStepExecution().getId();
        log.info("{} - Task {} Step execution ID {}, Step name {} - From {} to {}", associatedWithAction(), taskName,
            id, stepName, fromBranch, toBranch);
        return RepeatStatus.FINISHED;
    }
}
