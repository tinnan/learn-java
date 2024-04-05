package com.example.demo.batch.api;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile({"api"})
@RestController
@RequestMapping("/api/v1/job/run")
@AllArgsConstructor
public class JobRunnerController {
    private final JobLauncher apiJobLauncher;
    private final Job tut1Job;

    @PostMapping("tut1")
    public void startTut1() throws Exception {
        final JobParameters jobParameters = new JobParameters();
        apiJobLauncher.run(tut1Job, jobParameters);
    }
}
