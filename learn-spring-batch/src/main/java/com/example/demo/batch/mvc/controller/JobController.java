package com.example.demo.batch.mvc.controller;

import com.example.demo.batch.mvc.model.JobSummary;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/job")
@AllArgsConstructor
@Slf4j
public class JobController {
    private final JobOperator jobOperator;

    @GetMapping("/{jobName}")
    public String viewJob(@PathVariable("jobName") String jobName, Model model) {
        return "view-job";
    }

    @GetMapping("/{jobName}/history")
    public String viewJobHistory(@PathVariable("jobName") String jobName, Model model) throws NoSuchJobException,
            NoSuchJobInstanceException, NoSuchJobExecutionException {
        List<JobSummary> jobSummaries = new ArrayList<>();
        List<Long> instanceIds = jobOperator.getJobInstances(jobName, 0, 100);
        for (Long instanceId : instanceIds) {
            List<Long> executionIds = jobOperator.getExecutions(instanceId);
            for (Long executionId : executionIds) {
                String summary = jobOperator.getSummary(executionId);
                String endTime = "";
                String status = "";
                for (String sum : summary.split(",")) {
                    if (sum.contains("endTime")) {
                        endTime = sum.split("=")[1];
                    } else if (sum.contains(" status=")) {
                        status = sum.split("=")[1];
                    }
                }
                jobSummaries.add(new JobSummary(instanceId, executionId, endTime, status));
            }
        }
        model.addAttribute("jobSummaries", jobSummaries);
        return "view-job-history";
    }
}
