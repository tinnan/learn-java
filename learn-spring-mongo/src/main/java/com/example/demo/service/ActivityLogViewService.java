package com.example.demo.service;

import com.example.demo.domain.ActivityLogQueryParam;
import com.example.demo.domain.ActivityLogView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogViewService extends ActivityLogServiceBase {

    private final MongoTemplate mongoTemplate;

    public Page<ActivityLogView> query(ActivityLogQueryParam param) {
        param.getPaginationAndSort().setSortDesc("txDatetime");
        final long start = System.currentTimeMillis();
        try {
            long totalRecord = mongoTemplate.count(createQuery(param, true), ActivityLogView.class);
            Pageable pageable = createPageable(param);
            List<ActivityLogView> activityLogs = mongoTemplate.find(createQuery(param, pageable),
                ActivityLogView.class);
            return new PageImpl<>(activityLogs, pageable, totalRecord);
        } finally {
            final long end = System.currentTimeMillis();
            log.info("Query activity log data took: {} ms", end - start);
        }
    }
}
