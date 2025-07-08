package com.github.tinnan.jobrunner.task;

import com.github.tinnan.jobrunner.constants.JobStepAction;
import com.github.tinnan.jobrunner.constants.JobStepAction.ParamSpec;
import com.github.tinnan.jobrunner.constants.JobParameterName;
import com.github.tinnan.jobrunner.entity.StartJobParam;
import com.github.tinnan.jobrunner.exception.JobParameterViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;

public abstract class AbstractTasklet implements Tasklet {

    public AbstractTasklet(StartJobParam.Step jobParamStep) {
        validate(jobParamStep);
    }

    protected abstract JobStepAction associatedWithAction();

    public List<String> produceContextParams() {
        return Collections.emptyList();
    }

    protected void validate(StartJobParam.Step jobParamStep) {
        JobStepAction associatedWithAction = associatedWithAction();
        if (jobParamStep.getAction() != associatedWithAction) {
            throw new IllegalArgumentException(
                "Incorrect action (" + jobParamStep.getAction() + ") is passed. Expected: " + associatedWithAction);
        }

        List<ParamSpec> paramSpecs = Optional.ofNullable(jobParamStep.getAction().getParamSpecs())
            .orElse(Collections.emptyList());

        List<String> violations = new ArrayList<>();
        paramSpecs.forEach(paramSpec -> {
            JobParameterName parameterName = paramSpec.getParameterName();
            String parameterValue = jobParamStep.getParameterValue(parameterName);
            if (paramSpec.isRequired() && StringUtils.isBlank(parameterValue)) {
                violations.add("Parameter " + parameterName + " is required");
            }
            if (!isValidAgainstAvailableValues(parameterValue, paramSpec.getAvailableValues())) {
                violations.add("Parameter " + parameterName + " value (" + parameterValue
                    + ") did not match with available values");
            }
        });

        if (!violations.isEmpty()) {
            String msg = "Step " + jobParamStep.getAction() + " parameters violations: " + violations;
            throw new JobParameterViolationException(msg);
        }
    }

    protected void validateRequiredContextParams(StepContribution contribution, List<String> requiredParams) {
        List<String> violations = new ArrayList<>();
        for (String requiredParam : requiredParams) {
            if (!contribution.getStepExecution().getJobExecution().getExecutionContext().containsKey(requiredParam)) {
                violations.add(requiredParam);
            }
        }
        throw new JobParameterViolationException("Missing required context params from previous steps: " + violations);
    }

    private boolean isValidAgainstAvailableValues(String valueToCheck, String[] availableValues) {
        if (StringUtils.isEmpty(valueToCheck) || availableValues == null || availableValues.length == 0) {
            return true;
        }
        return Arrays.stream(availableValues).toList().contains(valueToCheck);
    }
}
