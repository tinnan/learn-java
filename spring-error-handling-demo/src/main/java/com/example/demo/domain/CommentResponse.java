package com.example.demo.domain;

import java.time.LocalDateTime;

public record CommentResponse(Long id, Long postId, String ownerUsername, LocalDateTime commentTs, String content) {
}
