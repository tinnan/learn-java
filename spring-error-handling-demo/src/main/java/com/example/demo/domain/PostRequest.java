package com.example.demo.domain;

public record PostRequest(String content) implements Containable {
    @Override
    public String getContent() {
        return content;
    }
}
