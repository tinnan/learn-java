package com.example.demo.controller;

import com.example.demo.clients.NotificationClient;
import com.example.demo.domain.Notification;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController implements NotificationClient {
    private final NotificationService notificationService;

    @Override
    public Notification notify(Notification notification) {
        return notificationService.notify(notification);
    }
}
