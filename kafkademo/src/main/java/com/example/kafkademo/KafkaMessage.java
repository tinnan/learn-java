package com.example.kafkademo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class KafkaMessage {
    @JsonProperty("custom_message")
    private String customMessage;
    private String memo;
}
