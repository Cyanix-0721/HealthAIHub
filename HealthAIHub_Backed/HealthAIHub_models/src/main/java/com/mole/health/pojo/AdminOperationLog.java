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
 * 管理员操作日志表
 *
 * @TableName admin_operation_log
 */
@TableName(value = "admin_operation_log")
@Data
@Schema(name = "AdminOperationLog", description = "管理员操作日志表")
public class AdminOperationLog implements Serializable {
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
     * 管理员ID
     */
    @TableField(value = "admin_id")
    @Schema(description = "管理员ID")
    private Long adminId;
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