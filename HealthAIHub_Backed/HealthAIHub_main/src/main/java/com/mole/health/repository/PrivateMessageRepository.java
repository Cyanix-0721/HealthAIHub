package com.mole.health.repository;

import com.mole.health.pojo.PrivateMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrivateMessageRepository extends MongoRepository<PrivateMessage, String> {
}
