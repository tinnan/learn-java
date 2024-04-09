package com.example.demo.domain;

public record CommentRequest(String content) implements Containable {
    @Override
    public String getContent() {
        return content;
    }
}
