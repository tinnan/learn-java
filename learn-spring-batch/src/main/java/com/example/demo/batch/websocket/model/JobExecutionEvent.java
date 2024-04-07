package com.example.demo.batch.websocket.model;

import java.time.LocalDateTime;

public record JobExecutionEvent(LocalDateTime ts, String event, String status) {
}
