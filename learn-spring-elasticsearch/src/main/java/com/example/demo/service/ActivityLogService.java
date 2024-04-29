package com.example.demo.service;

import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import com.example.demo.model.ActivityLog;
import com.example.demo.model.ActivityLogQueryParam;
import com.example.demo.model.ActivityLogQueryParam.PaginationAndSort;
import com.example.demo.repository.es.ActivityLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {
    private final ElasticsearchOperations elasticsearchOperations;
    private final ActivityLogRepository activityLogRepository;

    public List<ActivityLog> queryLogs(ActivityLogQueryParam param) {
        Criteria c = Criteria.where("txDatetime").between(param.getDateTimeFrom(), param.getDateTimeTo());
        if (param.getStaffId() != null) {
            c.and("staffId").is(param.getStaffId());
        }
        if (param.getServiceType() != null) {
            c.and("serviceType").is(param.getServiceType());
        }
        if (param.getBranchCode() != null) {
            c.and("branchCode").is(param.getBranchCode());
        }
        if (param.getChannel() != null) {
            c.and("channel").is(param.getChannel());
        }
        if (param.getRmidEc() != null) {
            c.and("rmidEc").is(param.getRmidEc());
        }
        if (param.getIdType() != null && param.getIdNo() != null) {
            c.and("idType").is(param.getIdType()).and("idNo").is(param.getIdNo());
        }
        if (param.getActivityType() != null) {
            c.and("activityType").in(param.getActivityType());
        }
        if (param.getActivityStatus() != null) {
            c.and("activityStatus").is(param.getActivityStatus());
        }

//        NativeQueryBuilder nativeQueryBuilder = NativeQuery.builder().withQuery(new CriteriaQuery(c));
        Query criteriaQuery = new CriteriaQuery(c);
        if (param.getPaginationAndSort() != null) {
            PaginationAndSort paginationAndSort = param.getPaginationAndSort();
            if (paginationAndSort.isSorted()) {
                Sort sort = Sort.by(paginationAndSort.getSortDirection(), paginationAndSort.getSortField());
//                nativeQueryBuilder.withSort(sort);
                criteriaQuery.addSort(sort);
            }
            if (paginationAndSort.isPaged()) {
                Pageable pageable = PageRequest.of(paginationAndSort.getPageNumber(), paginationAndSort.getPageSize());
//                nativeQueryBuilder.withPageable(pageable);
                criteriaQuery.setPageable(pageable);
            }
        }

        // !! NOTE: using CriteriaQuery directly with ElasticsearchOperations, ElasticsearchTemplate
        // causes error "all shard failed". -> The error occurs when org.springframework.data.domain.Sort
        // is included in Query.
        // todo: how to actually write query + pagination + sorting for Elasticsearch.
        List<ActivityLog> activityLogs = elasticsearchOperations
            .search(criteriaQuery, ActivityLog.class)
            .stream().map(SearchHit::getContent).toList();
//        List<ActivityLog> activityLogs = elasticsearchOperations
//            .search(nativeQueryBuilder.build(), ActivityLog.class)
//            .stream().map(SearchHit::getContent).toList();
        log.info("Search hit: {}", activityLogs);
        return activityLogs;
    }
}
