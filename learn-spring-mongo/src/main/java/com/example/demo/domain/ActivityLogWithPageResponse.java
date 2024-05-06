package com.example.demo.domain;

import java.util.List;

public record ActivityLogWithPageResponse(List<ActivityLog> activityLogs, PageInfo pageInfo) {

    public record PageInfo(Integer page, Integer pageSize, Integer totalPage, Boolean hasPrevious,
                           Boolean hasNext, Boolean lastPage) {

    }
}
