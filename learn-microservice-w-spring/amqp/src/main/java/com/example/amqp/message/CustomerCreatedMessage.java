package com.example.amqp.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record CustomerCreatedMessage(@JsonProperty("customer_id") Integer customerId,
                                     @JsonProperty("email") String email) implements Serializable {
}
