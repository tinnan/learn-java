package com.example.demo.controllerparam.controller;

import com.example.demo.controllerparam.client.ServerFeign;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoController {

    private final ServerFeign serverFeign;

    @GetMapping("/server")
    public GetParams serverGet(@RequestHeader HttpHeaders headers, @Valid GetParams params) {
        return params;
    }

    @GetMapping("/client")
    public GetParams clientGet(@RequestParam String name, @RequestParam(required = false) Integer age) {
        return serverFeign.serverGet(new HttpHeaders(), GetParams.builder().name(name).age(age).build());
    }

    @Getter
    @Builder
    public static class GetParams {

        @NotBlank
        private String name;
        @NotNull
        private Integer age;
    }
}
