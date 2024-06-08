package com.example.demo.domain.activitylog;

import java.util.List;

public record ActivityLogWithPageResponse(List<? extends ActivityLogBase> activityLogs, PageInfo pageInfo) {

    public record PageInfo(Integer page, Integer pageSize, Integer totalPage, Long totalRecord,
                           Boolean hasPrevious, Boolean hasNext, Boolean lastPage) {

    }
}
