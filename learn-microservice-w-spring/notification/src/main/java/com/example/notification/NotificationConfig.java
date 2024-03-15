package com.example.notification;

import com.example.common.Global;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(Global.RABBIT_EXCHANGE);
    }

    @Bean
    public Queue customerQueue() {
        return new Queue(Global.RABBIT_QUEUE_CUSTOMER);
    }

    @Bean
    public Binding binding(DirectExchange directExchange, Queue customerQueue) {
        return BindingBuilder.bind(customerQueue).to(directExchange).with("created");
    }
}
