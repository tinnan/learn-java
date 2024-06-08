package com.example.demo.service;

import com.example.demo.domain.activitylog.ActivityLogForAggregate;
import com.example.demo.domain.activitylog.ActivityLogQueryParam;
import com.example.demo.domain.activitylog.ActivityLogQueryParam.PaginationAndSort;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.DocumentOperators;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SetWindowFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.SetWindowFieldsOperation.ComputedField;
import org.springframework.data.mongodb.core.aggregation.SetWindowFieldsOperation.SetWindowFieldsOperationBuilder;
import org.springframework.data.mongodb.core.aggregation.SetWindowFieldsOperation.WindowOutput;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.StringOperators.Concat;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityLogAggregationService extends ActivityLogServiceBase {

    private final MongoTemplate mongoTemplate;

    public List<ActivityLogForAggregate> queryAggregation(ActivityLogQueryParam param) {
        Aggregation aggregate = createAggregateQuery(param);
        // Since each aggregation stages have 100MB memory limit set on them, if the dataset is too large
        // and memory exceeds the limit Mongo will produce error.
        // To allow pipeline to exceed the limit one may set option allowDiskUse to MongoTemplate.aggregate() method.
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        AggregationResults<ActivityLogForAggregate> aggregateResult = mongoTemplate.aggregate(
            aggregate.withOptions(options),
            ActivityLogForAggregate.class,
            ActivityLogForAggregate.class);
        return aggregateResult.getMappedResults();
    }

    private Aggregation createAggregateQuery(ActivityLogQueryParam param) {
        List<AggregationOperation> stages = new ArrayList<>();
        ProjectionOperation projectionStage = Aggregation.project(ActivityLogForAggregate.class)
            .and(Concat.valueOf("service_type").concat("_").concatValueOf("activity_status"))
            .as("user_activity");
        stages.add(projectionStage);

        Criteria c = Criteria.where("tx_datetime").gte(param.getDateTimeFrom()).lte(param.getDateTimeTo());
        if (param.getUserActivity() != null) {
            c.and("user_activity").is(param.getUserActivity());
        }
        MatchOperation matchStage = Aggregation.match(c);
        stages.add(matchStage);

        // This stage may produce Out of memory error when processing large dataset.
        SetWindowFieldsOperation setRowNumStage = new SetWindowFieldsOperationBuilder()
            .sortBy(Sort.by("tx_datetime").descending())
            .output(new WindowOutput(
                new ComputedField("row_num", DocumentOperators.documentNumber())
            )).build();
        stages.add(setRowNumStage);

        PaginationAndSort paginationAndSort = param.getPaginationAndSort();
        if (paginationAndSort != null && paginationAndSort.isPaged()) {
            SkipOperation skipStage = Aggregation.skip(
                (long) paginationAndSort.getPageNumber() * paginationAndSort.getPageSize());
            LimitOperation limitStage = Aggregation.limit(paginationAndSort.getPageSize());
            stages.add(skipStage);
            stages.add(limitStage);
        }

        return Aggregation.newAggregation(stages);
    }
}
