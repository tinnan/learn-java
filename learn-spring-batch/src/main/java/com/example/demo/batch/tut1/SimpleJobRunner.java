package com.example.demo.batch.tut1;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
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
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("file.input.classpath", "tut1/sample-data.csv");
        tut1JobLauncher.run(tut1Job, builder.toJobParameters());
    }
}
