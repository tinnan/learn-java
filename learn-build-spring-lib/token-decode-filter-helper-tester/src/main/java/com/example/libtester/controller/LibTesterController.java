package com.example.libtester.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/secure/filter")
@RestController
public class LibTesterController {
    @GetMapping("/inquire")
    @PreAuthorize("hasAuthority('INQUIRE')")
    public String inquire() {
        return "Hello inquire.";
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
