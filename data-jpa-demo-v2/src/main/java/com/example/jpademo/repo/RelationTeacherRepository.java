package com.example.jpademo.repo;

import com.example.jpademo.entity.RelationTeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationTeacherRepository extends JpaRepository<RelationTeacherEntity, Integer> {

}
