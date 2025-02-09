package com.example.jpademo.repo;

import com.example.jpademo.entity.RelationCourseEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationCourseRepository extends JpaRepository<RelationCourseEntity, Integer> {

    @Query("SELECT c FROM relation_course c WHERE c.code = :courseCode")
    List<RelationCourseEntity> findByCourseCode(String courseCode);
}
