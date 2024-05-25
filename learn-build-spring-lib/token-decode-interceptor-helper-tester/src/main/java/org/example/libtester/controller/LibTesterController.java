package org.example.libtester.controller;

import org.example.security.annotation.HasAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/secure/interceptor")
@RestController
public class LibTesterController {
    @GetMapping("/inquire")
    @HasAuthority("INQUIRE")
    public String inquire() {
        return "Hello inquire.";
    }

    @GetMapping("/generate")
    @HasAuthority("GENERATE")
    public String generate() {
        return "Hello generate.";
    }

    @GetMapping("/count")
    @HasAuthority("COUNT")
    public Integer count() {
        return 10;
    }

    @GetMapping("/sum")
    public Integer sum() {
        return 50;
    }
}
