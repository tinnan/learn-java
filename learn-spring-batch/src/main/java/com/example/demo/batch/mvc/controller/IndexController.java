package com.example.demo.batch.mvc.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Profile("api")
@Controller
@RequestMapping("/")
@Slf4j
@AllArgsConstructor
public class IndexController {
    private final JobOperator jobOperator;

    @GetMapping
    public String landing(Model model) {
        Set<String> jobNames = jobOperator.getJobNames();
        model.addAttribute("jobNames", jobNames);
        return "index";
    }
}
