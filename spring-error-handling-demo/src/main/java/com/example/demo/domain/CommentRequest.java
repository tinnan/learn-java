package com.example.demo.domain;

public record CommentRequest(Long postId, String content) implements Containable {
    @Override
    public String getContent() {
        return content;
    }
}
