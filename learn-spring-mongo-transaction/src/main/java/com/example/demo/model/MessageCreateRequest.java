package com.example.demo.model;

import com.example.demo.domain.ActivityLog;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
public class MessageCreateRequest {

    private Instant txDatetime;
    private String staffId;
    private String branchCode;
    private String channel;
    private Integer rmidEc;
    private String idType;
    private String idNo;
    private String serviceType;
    private String activityType;
    private String activityStatus;
    private String metaData;
    private ActivityLog.Detail detail;

    @NoArgsConstructor
    @Accessors(chain = true)
    @Data
    public static class Detail {

        private String errorCode;
        private String errorMsg;
        private String[] errorFields;
    }
}
