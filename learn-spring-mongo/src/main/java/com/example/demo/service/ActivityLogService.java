package com.example.demo.service;

import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.ActivityLogQueryParam;
import com.example.demo.domain.csv.ReportHeaderMappingStrategy;
import com.example.demo.repository.ActivityLogRepository;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService extends ActivityLogServiceBase {

    @Value("${activity-log.export.path}")
    private String exportFilePath;
    private final MongoTemplate mongoTemplate;
    private final ActivityLogRepository activityLogRepository;

    public ActivityLog create(ActivityLog activityLog) {
        return activityLogRepository.save(activityLog);
    }

    public List<ActivityLog> queryLogs(ActivityLogQueryParam param) {
        final long start = System.currentTimeMillis();
        List<ActivityLog> activityLogs;
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
}
