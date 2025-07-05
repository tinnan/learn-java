package com.github.tinnan.jobrunner.controller;

import com.github.tinnan.jobrunner.model.BatchJob;
import com.github.tinnan.jobrunner.model.JobStartResult;
import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.batch.core.JobExecution;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/job")
public interface JobController {

    @PostMapping("/start")
    JobStartResult startJob();

    @PostMapping("/{jobInstanceId}/retry")
    JobStartResult retryJob(@PathVariable Long jobInstanceId);

    @GetMapping
    List<BatchJob> fetchJobs(
        @RequestParam(required = false) @Nullable Long jobInstanceIdOffset,
        @RequestParam(required = false) @Nullable Long count);

    @GetMapping("/{jobInstanceId}")
    List<JobExecution> fetchJob(@PathVariable Long jobInstanceId);
}
