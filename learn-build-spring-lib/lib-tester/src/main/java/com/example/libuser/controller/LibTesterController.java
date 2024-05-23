package com.example.libuser.controller;

import com.example.libuser.service.TestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/secure")
@RestController
public class LibTesterController {
    private final TestService testService;

    public LibTesterController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/inquire")
    @PreAuthorize("hasAuthority('INQUIRE')")
    public String inquire() {
        return testService.inquire();
    }

    @GetMapping("/generate")
    @PreAuthorize("hasRole('GENERATE')")
    public String generate() {
        return "Hello generate.";
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('COUNT')")
    public Integer count() {
        return 10;
    }

    @GetMapping("/sum")
    public Integer sum() {
        return 50;
    }
}
