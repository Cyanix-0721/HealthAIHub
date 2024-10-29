package com.mole.health.repository;

import com.mole.health.pojo.ChatroomMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatroomMessageRepository extends MongoRepository<ChatroomMessage, String> {
}
