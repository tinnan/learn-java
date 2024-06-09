package com.example.demo.domain;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("activity_log")
@Data
public class ActivityLog {

    @Id
    private String id;

    @Field(name = "tx_datetime")
    private Instant txDatetime;

    @Field(name = "staff_id")
    private String staffId;

    @Field(name = "branch_code")
    private String branchCode;

    @Field(name = "channel")
    private String channel;

    @Field(name = "rmid_ec")
    private Integer rmidEc;

    @Field(name = "id_type")
    private String idType;

    @Field(name = "id_no")
    private String idNo;

    @Field(name = "service_type")
    private String serviceType;

    @Field(name = "activity_type")
    private String activityType;

    @Field(name = "activity_status")
    private String activityStatus;

    @Field(name = "meta_data")
    private String metaData;

    @Field(name = "detail")
    private ActivityLog.Detail detail;

    @NoArgsConstructor
    @Accessors(chain = true)
    @Data
    public static class Detail {

        @Field(name = "error_code")
        private String errorCode;
        @Field(name = "error_msg")
        private String errorMsg;
        @Field(name = "error_fields")
        private String[] errorFields;
    }
}
