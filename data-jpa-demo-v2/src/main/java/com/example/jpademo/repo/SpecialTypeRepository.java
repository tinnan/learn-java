package com.example.jpademo.repo;

import com.example.jpademo.entity.SpecialTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialTypeRepository extends JpaRepository<SpecialTypeEntity, Integer> {

}
