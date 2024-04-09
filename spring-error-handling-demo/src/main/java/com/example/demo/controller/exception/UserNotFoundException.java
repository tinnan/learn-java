package com.example.demo.controller.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends Exception {
    private final String username;

    public UserNotFoundException(String username) {
        super("Username [" + username + "] not found.");
        this.username = username;
    }
}
