package com.example.demo.service;

import com.example.demo.domain.Notification;
import com.example.demo.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public Notification notify(Notification notification) {
        log.info("Sent notification e-mail to customer.");
        return notificationRepository.save(notification);
    }
}
