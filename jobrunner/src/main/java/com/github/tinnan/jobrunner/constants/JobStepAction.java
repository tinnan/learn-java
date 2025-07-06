package com.github.tinnan.jobrunner.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobStepAction {

    MERGE("Merge"),
    DEV_BUILD_DEPLOY("DEV build-deploy"),
    SIT_BUILD("SIT build"),
    SIT_DEPLOY_DEV("SIT deploy dev"),
    DEPLOY_RELEASE("Deploy release");

    private final String desc;
}
