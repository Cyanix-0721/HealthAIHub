package com.mole.health.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@Document(value = "notification")
public class Notification {
    @Id
    private String id;

    @Field(value = "content")
    private String content;

    @Field(value = "is_read")
    private Boolean isRead;

    @Field(value = "timestamp")
    private Instant timestamp;

    @Field(value = "user_id")
    private String userId;
}