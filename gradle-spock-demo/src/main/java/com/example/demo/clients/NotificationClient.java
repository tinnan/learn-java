package com.example.demo.clients;

import com.example.demo.domain.Notification;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "notification", url = "${feign.client.notification.url}")
public interface NotificationClient {
    @PostMapping("/notify")
    Notification notify(Notification notification);
}
