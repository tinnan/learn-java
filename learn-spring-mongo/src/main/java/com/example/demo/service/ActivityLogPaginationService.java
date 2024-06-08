package com.example.demo.service;

import com.example.demo.domain.activitylog.ActivityLog;
import com.example.demo.domain.activitylog.ActivityLogQueryParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityLogPaginationService extends ActivityLogServiceBase {

    private final MongoTemplate mongoTemplate;

    public Page<ActivityLog> findWithPage(ActivityLogQueryParam param, int pageNumber, int pageSize) {
        Query query = createQuery(param);
        Pageable pageable = createPageable(pageNumber - 1, // Convert to zero-based page number.
            pageSize);
        long totalRecord = mongoTemplate.count(query, ActivityLog.class);
        query.with(pageable);
        query.with(Sort.by("txDatetime").descending());
        List<ActivityLog> activityLogs = mongoTemplate.find(query, ActivityLog.class);
        return new PageImpl<>(activityLogs, pageable, totalRecord);
    }
}
