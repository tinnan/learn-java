package com.github.tinnan.jobrunner.repository;

import com.github.tinnan.jobrunner.entity.BatchJobInstanceParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchJobInstanceParamRepository extends JpaRepository<BatchJobInstanceParam, Long> {

}
