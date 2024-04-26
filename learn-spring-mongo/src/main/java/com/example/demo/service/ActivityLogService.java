package com.example.demo.service;

import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.ActivityLogQueryParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final MongoTemplate mongoTemplate;

    public List<ActivityLog> queryLogs(ActivityLogQueryParam param) {
        Criteria c = Criteria.where("txDatetime").gte(param.getDateTimeFrom()).lte(param.getDateTimeTo());
        if (param.getServiceType() != null) {
            c.and("serviceType").is(param.getServiceType());
        }
        if (param.getBranchCode() != null) {
            c.and("branchCode").is(param.getBranchCode());
        }
        if (param.getChannel() != null) {
            c.and("channel").is(param.getChannel());
        }
        if (param.getIdType() != null && param.getIdNo() != null) {
            c.and("idType").is(param.getIdType()).and("idNo").is(param.getIdNo());
        }
        if (param.getActivityType() != null) {
            c.and("activityType").is(param.getActivityType());
        }
        if (param.getActivityStatus() != null) {
            c.and("activityStatus").is(param.getActivityStatus());
        }
        if (param.getRmidEc() != null) {
            c.and("rmidEc").is(param.getRmidEc());
        }
        Query query = new Query(c);
        if (param.getPaginationAndSort() != null) {
            if (param.getPaginationAndSort().isSort()) {
                Sort sort = Sort.by(param.getPaginationAndSort().getSortDirection(),
                    param.getPaginationAndSort().getSortField());
                query.with(sort);
            }
            if (param.getPaginationAndSort().getPageNumber() > 0) {
                Pageable page = PageRequest.of(param.getPaginationAndSort().getPageNumber() - 1,
                    param.getPaginationAndSort().getPageSize());
                query.with(page);
            }
        }
        return mongoTemplate.find(query, ActivityLog.class);
    }
}
