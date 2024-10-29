package com.mole.health.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@Document(value = "private_message")
public class PrivateMessage {
    @Id
    private String id;

    @Field(value = "is_deleted")
    private Boolean isDeleted;

    @Field(value = "receiver_id")
    private String receiverId;

    @Field(value = "sender_id")
    private String senderId;

    @Field(value = "content")
    private String content;

    @Field(value = "timestamp")
    private Instant timestamp;
}