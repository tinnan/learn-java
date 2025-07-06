package com.github.tinnan.jobrunner.constants;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobStepAction {

    MERGE("Merge", List.of(
        new ParamSpec(ParameterName.FROM_BRANCH, true, null),
        new ParamSpec(ParameterName.TO_BRANCH, true, null)
    )),
    DEV_BUILD_DEPLOY("DEV build-deploy", List.of(
        new ParamSpec(ParameterName.ENV, true, Env.names())
    )),
    SIT_BUILD("SIT build", List.of(
        new ParamSpec(ParameterName.RELEASE, true, null)
    )),
    SIT_DEPLOY_DEV("SIT deploy with dev tag", List.of(
        new ParamSpec(ParameterName.ENV, true, Env.names())
    )),
    DEPLOY_RELEASE("Deploy release", List.of(
        new ParamSpec(ParameterName.ENV, true, Env.names())
    ));

    private final String desc;
    private final List<ParamSpec> paramSpecs;

    @Getter
    @AllArgsConstructor
    public static class ParamSpec {

        private ParameterName parameterName;
        private boolean required;
        private String[] availableValues;
    }
}
