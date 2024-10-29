package com.mole.health.repository;

import com.mole.health.pojo.UserAiInteraction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserAiInteractionRepository extends MongoRepository<UserAiInteraction, String> {
    Optional<UserAiInteraction> findByUserIdAndSessionId(String userId, String sessionId);
}
