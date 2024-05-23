package com.example.libuser.service;

import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    @Override
    public String inquire() {
        return "Hello inquire.";
    }
}
