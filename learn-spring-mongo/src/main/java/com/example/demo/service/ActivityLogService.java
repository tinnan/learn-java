package com.example.demo.service;

import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.ActivityLogQueryParam;
import com.example.demo.domain.QActivityLog;
import com.example.demo.repository.ActivityLogRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {

    @Value("${activity-log.export.path}")
    private String exportFilePath;
    private final MongoTemplate mongoTemplate;
    private final ActivityLogRepository activityLogRepository;

    public List<ActivityLog> queryLogs(ActivityLogQueryParam param) {
        Query query = createQuery(param, false);
        return mongoTemplate.find(query, ActivityLog.class);
    }

    public void exportLogs(ActivityLogQueryParam param) {
        Query query = createQuery(param, true);
        long start = System.currentTimeMillis();
        final AtomicInteger counter = new AtomicInteger(0);
        try (
            Stream<ActivityLog> stream = mongoTemplate.stream(query, ActivityLog.class);
            BufferedWriter writer = new BufferedWriter(new FileWriter(exportFilePath, StandardCharsets.UTF_8))
        ) {
            List<String> headers = List.of("วันเวลาทำรายการ", "Staff ID", "Branch Code", "Channel", "RM ID/EC",
                "ID Type", "ID No.", "Service Type", "Activity Type", "Activity Status", "Detail");
            writer.write(toCsvRow(headers));
            writer.newLine();
            stream.forEach(activityLog -> {
                try {
                    List<String> detailRow = List.of(
                        activityLog.getTxDatetime().toString(),
                        activityLog.getStaffId(),
                        activityLog.getBranchCode(),
                        activityLog.getChannel(),
                        activityLog.getRmidEc().toString(),
                        activityLog.getIdType(),
                        activityLog.getIdNo(),
                        activityLog.getServiceType(),
                        activityLog.getActivityType(),
                        activityLog.getActivityStatus(),
                        activityLog.getDetail() == null ? "" : activityLog.getDetail().toString()
                    );
                    writer.write(toCsvRow(detailRow));
                    writer.newLine();
                    counter.getAndIncrement();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            writer.write(String.valueOf(counter.get()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            long end = System.currentTimeMillis();
            log.info("Export CSV file process took: {} ms", end - start);
        }
    }

    private String toCsvRow(List<String> dataRow) {
        return String.join(",", dataRow.stream().map(s -> "\"" + s + "\"").toArray(String[]::new));
    }

    private Query createQuery(ActivityLogQueryParam param, boolean ignorePagination) {
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
        Query query = new Query(c);
        if (param.getPaginationAndSort() != null) {
            if (param.getPaginationAndSort().isSorted()) {
                Sort sort = Sort.by(param.getPaginationAndSort().getSortDirection(),
                    param.getPaginationAndSort().getSortField());
                query.with(sort);
            }
            if (!ignorePagination && param.getPaginationAndSort().isPaged()) {
                Pageable page = PageRequest.of(param.getPaginationAndSort().getPageNumber(),
                    param.getPaginationAndSort().getPageSize());
                query.with(page);
            }
        }
        return query;
    }

    /*
        Query composing with Domain Specific Language (DSL).
     */
    public Pair<Page<ActivityLog>, List<ActivityLog>> queryDslLogs(ActivityLogQueryParam param) {
        QActivityLog qActivityLog = QActivityLog.activityLog;
        BooleanExpression expression = qActivityLog.txDatetime.between(param.getDateTimeFrom(), param.getDateTimeTo());
        if (param.getServiceType() != null) {
            expression = expression.and(qActivityLog.serviceType.eq(param.getServiceType()));
        }
        if (param.getBranchCode() != null) {
            expression = expression.and(qActivityLog.branchCode.eq(param.getBranchCode()));
        }
        if (param.getChannel() != null) {
            expression = expression.and(qActivityLog.channel.eq(param.getChannel()));
        }
        if (param.getIdType() != null && param.getIdNo() != null) {
            expression =
                expression.and(qActivityLog.idType.eq(param.getIdType())).and(qActivityLog.idNo.eq(param.getIdNo()));
        }
        if (param.getActivityType() != null) {
            expression = expression.and(qActivityLog.activityType.in(param.getActivityType()));
        }
        if (param.getActivityStatus() != null) {
            expression = expression.and(qActivityLog.activityStatus.eq(param.getActivityStatus()));
        }
        if (param.getRmidEc() != null) {
            expression = expression.and(qActivityLog.rmidEc.eq(param.getRmidEc()));
        }

        ActivityLogQueryParam.PaginationAndSort paginationAndSort = param.getPaginationAndSort();
        Pageable pageable = null;
        Sort sort = null;
        if (paginationAndSort != null) {
            if (paginationAndSort.isSorted()) {
                sort = Sort.by(paginationAndSort.getSortDirection(), paginationAndSort.getSortField());
            }
            if (paginationAndSort.isPaged() && sort != null) {
                // Pagination with sorting.
                pageable = PageRequest.of(paginationAndSort.getPageNumber(), paginationAndSort.getPageSize(), sort);
            } else if (paginationAndSort.isPaged()) {
                pageable = PageRequest.of(paginationAndSort.getPageNumber(), paginationAndSort.getPageSize());
            }
        }

        if (pageable != null) {
            Page<ActivityLog> page = activityLogRepository.findAll(expression, pageable);
            return Pair.of(page, page.toList());
        } else if (sort != null) {
            List<ActivityLog> activityLogs = activityLogRepository.findAll(expression, sort);
            return Pair.of(Page.empty(Pageable.unpaged(sort)), activityLogs);
        }
        return Pair.of(Page.empty(), activityLogRepository.findAll(expression));
    }
}
