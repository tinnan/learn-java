package com.github.tinnan.jobrunner.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.tinnan.jobrunner.constants.JobStepAction;
import com.github.tinnan.jobrunner.constants.JobParameterName;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartJobParam implements Serializable {

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

        @JsonIgnore
        public String getParameterValue(JobParameterName parameterName) {
            return Optional.ofNullable(this.params).orElse(Collections.emptyList())
                .stream()
                .filter(p -> parameterName == p.getParameterName())
                .findFirst()
                .map(StepParam::getParameterValue)
                .orElse(null);
        }

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class StepParam implements Serializable {

            @Serial
            private static final long serialVersionUID = -7886373771601148091L;
            private JobParameterName parameterName;
            private String parameterValue;
        }
    }
}
