package com.example.demo.feignconfig.controller;

import com.example.demo.feignconfig.clients.ServerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final ServerClient serverClient;

    @GetMapping
    public String get() {
        return serverClient.get();
    }

    @PostMapping
    public String post() {
        return serverClient.post();
    }
}
