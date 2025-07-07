package com.github.tinnan.jobrunner.controller;

import com.github.tinnan.jobrunner.entity.StartJobParam;
import com.github.tinnan.jobrunner.model.BatchJob;
import com.github.tinnan.jobrunner.model.BatchJobDetail;
import com.github.tinnan.jobrunner.model.JobStartResult;
import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/job")
public interface JobController {

    @PostMapping("/start")
    JobStartResult startJob(@RequestBody StartJobParam jobParam);

    @PostMapping("/{jobInstanceId}/retry")
    JobStartResult retryJob(@PathVariable Long jobInstanceId);

    @GetMapping
    List<BatchJob> fetchJobs(
        @RequestParam(required = false) @Nullable Long jobInstanceIdOffset,
        @RequestParam(required = false) @Nullable Long count);

    @GetMapping("/{jobInstanceId}")
    BatchJobDetail fetchJob(@PathVariable Long jobInstanceId);
}
