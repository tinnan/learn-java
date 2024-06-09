package com.example.demo.controller;

import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.ActivityMessage;
import com.example.demo.model.ActivityMessageRequest;
import com.example.demo.model.MessageCreateRequest;
import com.example.demo.service.ActivityMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/activity")
@RequiredArgsConstructor
public class ActivityMessageController {

    private final ActivityMessageService activityMessageService;

    @PostMapping("/message")
    public ResponseEntity<ActivityMessage> createMessage(@RequestBody MessageCreateRequest request)
        throws JsonProcessingException {

        ActivityMessage created = activityMessageService.createMessage(request);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/message/process")
    public ResponseEntity<Void> processMessage(@RequestBody ActivityMessageRequest request) throws Exception {
        activityMessageService.saveMessageToActivityLog(request.getId(), request.isFakeError());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/message")
    public ResponseEntity<List<ActivityMessage>> getMessages() {
        return ResponseEntity.ok(activityMessageService.getMessages());
    }

    @GetMapping
    public ResponseEntity<List<ActivityLog>> getActivityLogs() {
        return ResponseEntity.ok(activityMessageService.getActivityLogs());
    }

    @DeleteMapping("/reset")
    public ResponseEntity<Void> resetAllData() {
        activityMessageService.resetData();
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }
}
