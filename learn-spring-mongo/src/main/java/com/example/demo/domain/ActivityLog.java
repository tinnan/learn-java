package com.example.demo.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("activity_log")
@Getter
@Setter
public class ActivityLog {
    @Id
    private Long id;
    private LocalDateTime txDatetime;
    private String staffId;
    private String branchCode;
    private String channel;
    private Integer rmidEc;
    private String idType;
    private String idNo;
    private String serviceType;
    private String activityType;
    private String activityStatus;
    private Detail detail;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Detail {
        private String errorCode;
        private String errorMsg;
    }
}
