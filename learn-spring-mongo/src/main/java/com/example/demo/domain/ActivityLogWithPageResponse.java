package com.example.demo.domain;

import java.util.List;

public record ActivityLogWithPageResponse(List<? extends ActivityLog> activityLogs, PageInfo pageInfo) {

    public record PageInfo(Integer page, Integer pageSize, Integer totalPage, Long totalRecord,
                           Boolean hasPrevious, Boolean hasNext, Boolean lastPage) {

    }
}
