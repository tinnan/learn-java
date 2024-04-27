package com.example.demo.repository;

import com.example.demo.domain.ActivityLog;
import java.math.BigInteger;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ActivityLogRepository extends MongoRepository<ActivityLog, BigInteger>,
    ListQuerydslPredicateExecutor<ActivityLog> {

    List<ActivityLog> findByStaffId(String staffId);
}
