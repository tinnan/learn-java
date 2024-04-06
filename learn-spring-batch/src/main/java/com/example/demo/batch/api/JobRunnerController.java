package com.example.demo.batch.api;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile({"api"})
@RestController
@RequestMapping("/api/v1/job/run")
@AllArgsConstructor
public class JobRunnerController {
    private final JobOperator apiJobOperator;

    @PostMapping("{jobName}")
    public void startJob(@PathVariable("jobName") String jobName) throws Exception {
        final DefaultJobParametersConverter parametersConverter = new DefaultJobParametersConverter();
        apiJobOperator.start(jobName, parametersConverter.getProperties(getJobParameters(jobName)));
    }

    private JobParameters getJobParameters(String jobName) {
        JobParametersBuilder builder = new JobParametersBuilder();
        if ("tut1Job".equals(jobName)) {
            builder.addString("file.input.classpath", "tut1/sample-data.csv");
        }
        return builder.toJobParameters();
    }
}
