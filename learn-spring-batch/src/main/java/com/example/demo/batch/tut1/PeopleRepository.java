package com.example.demo.batch.tut1;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile({ "tut1" })
public interface PeopleRepository extends JpaRepository<People, Long> {
}
