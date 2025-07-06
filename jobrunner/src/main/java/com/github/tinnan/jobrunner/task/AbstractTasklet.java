package com.github.tinnan.jobrunner.task;

import com.github.tinnan.jobrunner.constants.JobStepAction;
import com.github.tinnan.jobrunner.constants.JobStepAction.ParamSpec;
import com.github.tinnan.jobrunner.constants.ParameterName;
import com.github.tinnan.jobrunner.entity.JobParam;
import com.github.tinnan.jobrunner.exception.JobParameterViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.step.tasklet.Tasklet;

public abstract class AbstractTasklet implements Tasklet {

    public AbstractTasklet(JobParam.Step jobParamStep) {
        validate(jobParamStep);
    }

    protected abstract JobStepAction associatedWithAction();

    protected void validate(JobParam.Step jobParamStep) {
        JobStepAction associatedWithAction = associatedWithAction();
        if (jobParamStep.getAction() != associatedWithAction) {
            throw new IllegalArgumentException(
                "Incorrect action (" + jobParamStep.getAction() + ") is passed. Expected: " + associatedWithAction);
        }

        List<ParamSpec> paramSpecs = Optional.ofNullable(jobParamStep.getAction().getParamSpecs())
            .orElse(Collections.emptyList());

        List<String> violations = new ArrayList<>();
        paramSpecs.forEach(paramSpec -> {
            ParameterName parameterName = paramSpec.getParameterName();
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

    private boolean isValidAgainstAvailableValues(String valueToCheck, String[] availableValues) {
        if (StringUtils.isEmpty(valueToCheck) || availableValues == null || availableValues.length == 0) {
            return true;
        }
        return Arrays.stream(availableValues).toList().contains(valueToCheck);
    }
}
