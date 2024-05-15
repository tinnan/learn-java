package com.example.demo.repository;

import com.example.demo.domain.ActivityLog;
import java.math.BigInteger;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityLogRepository extends MongoRepository<ActivityLog, BigInteger> {

    List<ActivityLog> findByStaffId(String staffId);
}
