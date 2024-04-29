package com.example.demo.model;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.ValueConverter;

@Document(indexName = "admin_panel")
@Data
public class ActivityLog {
    @Id
    private String id;
    @Field(type = FieldType.Date, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX||uuuu-MM-dd'T'HH:mm:ss.SSS", format = {})
    @ValueConverter(CustomZonedTimeConverter.class)
    private ZonedDateTime txDatetime;
    @Field(type = FieldType.Text)
    private String staffId;
    @Field(type = FieldType.Text)
    private String branchCode;
    @Field(type = FieldType.Text)
    private String channel;
    @Field(type = FieldType.Integer)
    private Integer rmidEc;
    @Field(type = FieldType.Text)
    private String idType;
    @Field(type = FieldType.Text)
    private String idNo;
    @Field(type = FieldType.Text)
    private String serviceType;
    @Field(type = FieldType.Text)
    private String activityType;
    @Field(type = FieldType.Text)
    private String activityStatus;
    @Field(type = FieldType.Nested)
    private Detail detail;

    @AllArgsConstructor
    @Data
    public static class Detail {
        @Field(type = FieldType.Text)
        private String errorCode;
        @Field(type = FieldType.Text)
        private String errorMsg;
    }
}
