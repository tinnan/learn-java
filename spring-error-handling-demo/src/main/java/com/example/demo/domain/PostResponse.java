package com.example.demo.domain;

import java.time.LocalDateTime;

public record PostResponse(Long id, String ownerUsername, LocalDateTime postTs, String content) {
}
