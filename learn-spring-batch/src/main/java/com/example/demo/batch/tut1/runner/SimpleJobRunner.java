package com.example.demo.batch.tut1.runner;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"tut1-simple"})
@Configuration
@AllArgsConstructor
public class SimpleJobRunner implements CommandLineRunner {
    private final JobLauncher simpleJobLauncher;
    private final Job importUserJob;

    @Override
    public void run(String... args) throws Exception {
        JobParameters jobParameters = new JobParameters();
        simpleJobLauncher.run(importUserJob, jobParameters);
    }
}
