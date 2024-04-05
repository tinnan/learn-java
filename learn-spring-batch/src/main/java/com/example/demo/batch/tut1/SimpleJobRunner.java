package com.example.demo.batch.tut1;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"tut1"})
@Configuration
@AllArgsConstructor
public class SimpleJobRunner implements CommandLineRunner {
    private final JobLauncher tut1JobLauncher;
    private final Job tut1Job;

    @Override
    public void run(String... args) throws Exception {
        JobParameters jobParameters = new JobParameters();
        tut1JobLauncher.run(tut1Job, jobParameters);
    }
}
