package com.example.demo.service;

import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.ActivityLogQueryParam;
import com.example.demo.domain.QActivityLog;
import com.example.demo.domain.csv.ReportHeaderMappingStrategy;
import com.example.demo.repository.ActivityLogRepository;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
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
        final long start = System.currentTimeMillis();
        List<ActivityLog> activityLogs = null;
        try {
            Query query = createQuery(param, false);
            activityLogs = mongoTemplate.find(query, ActivityLog.class);
        } finally {
            final long end = System.currentTimeMillis();
            log.info("Query activity log data took: {} ms", end - start);
        }
        return activityLogs;
    }

    public String beanToCsvText(List<ActivityLog> activityLogs)
        throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        long start = System.currentTimeMillis();
        try (StringWriter stringWriter = new StringWriter()) {
            StatefulBeanToCsv<ActivityLog> csvWriter = createCsvWriter(stringWriter);
            csvWriter.write(activityLogs);
            return stringWriter.toString();
        } finally {
            long end = System.currentTimeMillis();
            log.info("Create CSV download data took: {} ms", end - start);
        }
    }

    public String exportLogsWithStream(ActivityLogQueryParam param) {
        Query query = createQuery(param, true);
        long start = System.currentTimeMillis();
        try (
            Stream<ActivityLog> stream = mongoTemplate.stream(query, ActivityLog.class);
            FileOutputStream fos = new FileOutputStream(exportFilePath);
            OutputStreamWriter osw = new OutputStreamWriter(fos)
        ) {
            StatefulBeanToCsv<ActivityLog> csvWriter = createCsvWriter(osw);
            csvWriter.write(stream);
            return exportFilePath;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        } finally {
            long end = System.currentTimeMillis();
            log.info("Export CSV file process with stream API took: {} ms", end - start);
        }
    }

    public void exportLogs(ActivityLogQueryParam param) {
        Query query = createQuery(param, true);
        long start;
        long end;
        start = System.currentTimeMillis();
        List<ActivityLog> activityLogs = mongoTemplate.find(query, ActivityLog.class);
        end = System.currentTimeMillis();
        log.info("Query data took: {} ms", end - start);

        start = System.currentTimeMillis();

        try (
            FileOutputStream fos = new FileOutputStream(exportFilePath);
            OutputStreamWriter osw = new OutputStreamWriter(fos)
        ) {
            StatefulBeanToCsv<ActivityLog> csvWriter = createCsvWriter(osw);
            csvWriter.write(activityLogs);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        } finally {
            end = System.currentTimeMillis();
            log.info("Export file took: {} ms", end - start);
        }
    }

    private StatefulBeanToCsv<ActivityLog> createCsvWriter(Writer fileWriter) {
        ReportHeaderMappingStrategy<ActivityLog> strategy = new ReportHeaderMappingStrategy<>();
        strategy.setType(ActivityLog.class);
        StatefulBeanToCsvBuilder<ActivityLog> csvBuilder = new StatefulBeanToCsvBuilder<>(fileWriter);
        return csvBuilder.withMappingStrategy(strategy).build();
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
