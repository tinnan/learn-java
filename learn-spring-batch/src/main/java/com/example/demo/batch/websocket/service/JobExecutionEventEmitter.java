package com.example.demo.batch.websocket.service;

import com.example.demo.batch.websocket.model.JobExecutionEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Profile("api")
@Service
@AllArgsConstructor
public class JobExecutionEventEmitter {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void publish(LocalDateTime ts, String event, String status) {
        simpMessagingTemplate.convertAndSend("/topic/job/event", new JobExecutionEvent(ts, event, status));
    }
}
