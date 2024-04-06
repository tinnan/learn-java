package com.example.demo.batch.api;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Profile({"api"})
@RestController
@RequestMapping("/api/v1/job")
@AllArgsConstructor
public class JobExplorerController {
    private final JobOperator apiJobOperator;

    @GetMapping
    public Set<String> listAllJobs() {
        return apiJobOperator.getJobNames();
    }
}
