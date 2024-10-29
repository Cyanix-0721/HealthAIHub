package com.mole.health.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@Document(value = "chatroom_message")
public class ChatroomMessage {
    @Id
    private String id;

    @Field(value = "content")
    private String content;

    @Field(value = "is_deleted")
    private Boolean isDeleted;

    @Field(value = "timestamp")
    private Instant timestamp;

    @Field(value = "user_id")
    private String userId;
}