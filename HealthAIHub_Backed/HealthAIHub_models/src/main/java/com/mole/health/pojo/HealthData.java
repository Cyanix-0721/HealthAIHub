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
 * 健康数据表
 *
 * @TableName health_data
 */
@TableName(value = "health_data")
@Data
@Schema(name = "HealthData", description = "健康数据表")
public class HealthData implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 数据ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "数据ID")
    private Long id;
    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @Schema(description = "用户ID")
    private Long userId;
    /**
     * 身高
     */
    @TableField(value = "height")
    @Schema(description = "身高")
    private Double height;
    /**
     * 体重
     */
    @TableField(value = "weight")
    @Schema(description = "体重")
    private Double weight;
    /**
     * 心率
     */
    @TableField(value = "heart_rate")
    @Schema(description = "心率")
    private Integer heartRate;
    /**
     * 血压
     */
    @TableField(value = "blood_pressure")
    @Schema(description = "血压")
    private String bloodPressure;
    /**
     * 血氧
     */
    @TableField(value = "blood_oxygen")
    @Schema(description = "血氧")
    private Integer bloodOxygen;
    /**
     * 血糖
     */
    @TableField(value = "blood_sugar")
    @Schema(description = "血糖")
    private Double bloodSugar;
    /**
     * 更新日期
     */
    @TableField(value = "updated_at")
    @Schema(description = "更新日期")
    private LocalDateTime updatedAt;
}
