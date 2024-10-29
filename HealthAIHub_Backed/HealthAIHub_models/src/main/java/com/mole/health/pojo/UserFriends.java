package com.mole.health.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户好友表
 *
 * @TableName user_friends
 */
@TableName(value = "user_friends")
@Data
@Schema(name = "UserFriends", description = "用户好友表")
public class UserFriends implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "记录ID")
    private Long id;
    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @Schema(description = "用户ID")
    private Long userId;
    /**
     * 好友的用户ID
     */
    @TableField(value = "friend_id")
    @Schema(description = "好友的用户ID")
    private Long friendId;
    /**
     * 好友状态（1=已添加，0=请求中，-1=已拒绝）
     */
    @TableField(value = "status")
    @Schema(description = "好友状态（1=已添加，0=请求中，-1=已拒绝）")
    private Integer status;
    /**
     * 添加好友的时间
     */
    @TableField(value = "created_at")
    @Schema(description = "添加好友的时间")
    private LocalDateTime createdAt;
}