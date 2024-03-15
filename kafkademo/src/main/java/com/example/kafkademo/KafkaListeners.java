package com.example.kafkademo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaListeners {

    @KafkaListener(topics = "demo", groupId = "foo")
    void listener(KafkaMessage data) {
        log.info("Message received: {}", data);
    }
}
