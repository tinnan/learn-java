package com.example.demo.domain;

import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("activity_log")
@QueryEntity
public class ActivityLog extends ActivityLogBase {

}
