package com.example.demo.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("activity_log_view")
@Getter
@Setter
public class ActivityLogView extends ActivityLogBase {

    @Field(name = "user_activity")
    private String userActivity;
}
