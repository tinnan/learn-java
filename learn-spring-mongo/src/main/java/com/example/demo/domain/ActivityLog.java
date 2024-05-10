package com.example.demo.domain;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
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
    @CsvBindByPosition(position = 0)
    @CsvBindByName(column = "Transaction Date-Time")
    @CsvDate("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime txDatetime;

    @Field(name = "staff_id")
    @CsvBindByPosition(position = 1)
    @CsvBindByName(column = "Staff ID")
    private String staffId;

    @Field(name = "branch_code")
    @CsvBindByPosition(position = 2)
    @CsvBindByName(column = "Branch Code")
    private String branchCode;

    @Field(name = "channel")
    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "Channel")
    private String channel;

    @Field(name = "rmid_ec")
    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "RM ID/EC")
    private Integer rmidEc;

    @Field(name = "id_type")
    @CsvBindByPosition(position = 5)
    @CsvBindByName(column = "ID Type")
    private String idType;

    @Field(name = "id_no")
    @CsvBindByPosition(position = 6)
    @CsvBindByName(column = "ID No.")
    private String idNo;

    @Field(name = "service_type")
    @CsvBindByPosition(position = 7)
    @CsvBindByName(column = "Service Type")
    private String serviceType;

    @Field(name = "activity_type")
    @CsvBindByPosition(position = 8)
    @CsvBindByName(column = "Activity Type")
    private String activityType;

    @Field(name = "activity_status")
    @CsvBindByPosition(position = 9)
    @CsvBindByName(column = "Activity Status")
    private String activityStatus;

    @Field(name = "detail")
    @CsvBindByPosition(position = 10)
    @CsvBindByName(column = "Detail")
    private Detail detail;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Detail {

        @Field(name = "error_code")
        private String errorCode;
        @Field(name = "error_msg")
        private String errorMsg;

        @Override
        public String toString() {
            return "errorCode=" + errorCode + ",errorMsg=" + errorMsg;
        }
    }
}
