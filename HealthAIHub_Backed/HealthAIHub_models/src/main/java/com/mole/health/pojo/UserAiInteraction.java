package com.mole.health.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "user_ai_interaction")
public class UserAiInteraction {
    @Id
    private String id;

    @Field(value = "user_id")
    private String userId;

    @Field(value = "session_id")
    private String sessionId;

    @Field(value = "context")
    private List<Message> context;

    @Field(value = "timestamp")
    private Instant timestamp;

    @Data
    @Builder
    public static class Message {
        private String role;
        private String content;
    }
}
