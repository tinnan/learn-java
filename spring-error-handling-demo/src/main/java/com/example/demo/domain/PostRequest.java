package com.example.demo.domain;

import java.time.LocalDateTime;

public record PostRequest(String content) implements Containable {
    @Override
    public String getContent() {
        return content;
    }
}
