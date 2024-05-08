package com.example.demo.service;

import com.example.demo.domain.ActivityLogQueryParam;
import com.example.demo.domain.ActivityLogQueryParam.PaginationAndSort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public abstract class ActivityLogServiceBase {

    protected Query createQuery(ActivityLogQueryParam param, boolean ignorePagination) {
        Query query = createQuery(param);
        query.with(createSort(param));
        PaginationAndSort paginationAndSort = param.getPaginationAndSort();
        if (!ignorePagination && paginationAndSort != null && paginationAndSort.isPaged()) {
            query.with(createPageable(paginationAndSort.getPageNumber(), paginationAndSort.getPageSize()));
        }
        return query;
    }

    protected Query createQuery(ActivityLogQueryParam param) {
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
            c.and("activityType").in(param.getActivityType());
        }
        if (param.getActivityStatus() != null) {
            c.and("activityStatus").is(param.getActivityStatus());
        }
        if (param.getRmidEc() != null) {
            c.and("rmidEc").is(param.getRmidEc());
        }
        return new Query(c);
    }

    protected Sort createSort(ActivityLogQueryParam param) {
        if (param.getPaginationAndSort() == null || !param.getPaginationAndSort().isSorted()) {
            return Sort.unsorted();
        }

        return Sort.by(param.getPaginationAndSort().getSortDirection(),
            param.getPaginationAndSort().getSortField());
    }

    protected Pageable createPageable(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }
}
