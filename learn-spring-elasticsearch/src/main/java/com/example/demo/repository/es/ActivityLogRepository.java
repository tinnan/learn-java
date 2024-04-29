package com.example.demo.repository.es;

import com.example.demo.model.ActivityLog;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends ElasticsearchRepository<ActivityLog, String> {

    @Override
    List<ActivityLog> findAll();
}
