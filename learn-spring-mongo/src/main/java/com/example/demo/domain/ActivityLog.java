package com.example.demo.domain;

import com.querydsl.core.annotations.QueryEntity;
import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("activity_log")
@QueryEntity
@Getter
@Setter
@ToString
public class ActivityLog {

    @Id
    private BigInteger id;
    @Field(name = "tx_datetime")
    private LocalDateTime txDatetime;
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
    @Field(name = "detail")
    private Detail detail;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Detail {

        private String errorCode;
        private String errorMsg;

        @Override
        public String toString() {
            return "errorCode=" + errorCode + ",errorMsg=" + errorMsg;
        }
    }
}
