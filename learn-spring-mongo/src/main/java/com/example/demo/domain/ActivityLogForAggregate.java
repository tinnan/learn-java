package com.example.demo.domain;

import org.springframework.data.mongodb.core.mapping.Field;

public class ActivityLogForAggregate extends ActivityLog {

    @Field(name = "user_activity")
    private String userActivity;

    @Field(name = "row_num")
    private Long rowNum;
}
