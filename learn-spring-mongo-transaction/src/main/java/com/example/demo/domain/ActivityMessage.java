package com.example.demo.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("activity_message")
@Data
@Builder
public class ActivityMessage {

    @Id
    private String id;
    @Field("data")
    private String data;
    @Field("process_status")
    private String processStatus;
}
