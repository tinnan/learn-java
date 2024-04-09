package com.example.demo.domain;

import java.time.LocalDate;

public record UserResponse(String username, String displayName, LocalDate joinDate) {
}
