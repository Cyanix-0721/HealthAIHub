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
 * 用户操作日志表
 *
 * @TableName user_operation_log
 */
@TableName(value = "user_operation_log")
@Data
@Schema(name = "UserOperationLog", description = "用户操作日志表")
public class UserOperationLog implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "日志ID")
    private Long id;
    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @Schema(description = "用户ID")
    private Long userId;
    /**
     * 操作内容
     */
    @TableField(value = "operation")
    @Schema(description = "操作内容")
    private String operation;
    /**
     * 操作时间
     */
    @TableField(value = "timestamp")
    @Schema(description = "操作时间")
    private LocalDateTime timestamp;
}