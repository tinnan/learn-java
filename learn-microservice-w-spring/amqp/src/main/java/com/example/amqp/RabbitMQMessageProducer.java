package com.example.amqp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class RabbitMQMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publish(Object payload, String exchangeName, String routingKey) {
        log.info("Publishing to {} using routing key {}. Payload: {}", exchangeName, routingKey, payload);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, payload);
        log.info("Message published.");
    }
}
