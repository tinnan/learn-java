package com.example.demo.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class UserService {

    public String getUser(
        @Valid
        @NotBlank(message = "{validation.email.notBlank}")
        @Email(message = "{validation.email.email}") String email) {

        return "{\"email\":\"" + email + "\"}";
    }
}
