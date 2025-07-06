package com.github.tinnan.jobrunner.entity;

import com.github.tinnan.jobrunner.constants.JobStepAction;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobParam implements Serializable {

    @Serial
    private static final long serialVersionUID = -180321234960237537L;
    private List<String> services;
    private List<Step> steps;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Step implements Serializable {

        @Serial
        private static final long serialVersionUID = 2663888680205213120L;
        private JobStepAction action;
        private List<StepParam> params;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class StepParam implements Serializable {

            @Serial
            private static final long serialVersionUID = -7886373771601148091L;
            private String parameterName;
            private String parameterValue;
        }
    }
}
