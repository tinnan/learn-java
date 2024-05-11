package com.example.demo.repo;

import com.example.demo.domain.FraudRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudCheckRepository extends JpaRepository<FraudRecord, Long> {
    Optional<FraudRecord> findByCustomerEmail(String customerEmail);
}
