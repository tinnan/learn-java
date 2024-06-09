package com.example.demo.repository;

import com.example.demo.domain.ActivityMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityMessageRepository extends MongoRepository<ActivityMessage, String> {

}
