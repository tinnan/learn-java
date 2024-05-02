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
    @CsvBindByName(column = "Transaction Date-Time")
    @CsvBindByPosition(position = 0)
    @CsvDate("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime txDatetime;

    @Field(name = "staff_id")
    @CsvBindByName(column = "Staff ID")
    @CsvBindByPosition(position = 1)
    private String staffId;

    @Field(name = "branch_code")
    @CsvBindByName(column = "Branch Code")
    @CsvBindByPosition(position = 2)
    private String branchCode;

    @Field(name = "channel")
    @CsvBindByName(column = "Channel")
    @CsvBindByPosition(position = 3)
    private String channel;

    @Field(name = "rmid_ec")
    @CsvBindByName(column = "RM ID/EC")
    @CsvBindByPosition(position = 4)
    private Integer rmidEc;

    @Field(name = "id_type")
    @CsvBindByName(column = "ID Type")
    @CsvBindByPosition(position = 5)
    private String idType;

    @Field(name = "id_no")
    @CsvBindByName(column = "ID No.")
    @CsvBindByPosition(position = 6)
    private String idNo;

    @Field(name = "service_type")
    @CsvBindByName(column = "Service Type")
    @CsvBindByPosition(position = 7)
    private String serviceType;

    @Field(name = "activity_type")
    @CsvBindByName(column = "Activity Type")
    @CsvBindByPosition(position = 8)
    private String activityType;

    @Field(name = "activity_status")
    @CsvBindByName(column = "Activity Status")
    @CsvBindByPosition(position = 9)
    private String activityStatus;

    @Field(name = "detail")
    @CsvBindByName(column = "Detail")
    @CsvBindByPosition(position = 10)
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
