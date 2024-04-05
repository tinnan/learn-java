package com.example.demo.batch.tut1.runner;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile({"tut1-api"})
@RestController
@RequestMapping("/api/v1/job/tut1")
@AllArgsConstructor
public class JobRunnerController {
    private final JobLauncher apiJobLauncher;
    private final Job importUserJob;

    @PostMapping
    public void start() throws Exception {
        final JobParameters jobParameters = new JobParameters();
        apiJobLauncher.run(importUserJob, jobParameters);
    }
}
