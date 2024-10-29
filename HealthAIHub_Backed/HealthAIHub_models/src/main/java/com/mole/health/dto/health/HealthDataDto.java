package com.mole.health.dto.health;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "HealthDataDto", description = "健康数据传输对象")
public class HealthDataDto {

    @Schema(description = "身高(cm)")
    private Double height;

    @Schema(description = "体重(kg)")
    private Double weight;

    @Schema(description = "心率(次/分)")
    private Integer heartRate;

    @Schema(description = "血压(mmHg)")
    private String bloodPressure;

    @Schema(description = "血氧饱和度(%)")
    private Integer bloodOxygen;

    @Schema(description = "血糖(mmol/L)")
    private Double bloodSugar;

    @Schema(description = "更新日期")
    private LocalDateTime updatedAt;

    @Schema(description = "BMI指数")
    private Double bmi;

    @Schema(description = "健康状态评估")
    private String healthStatus;

    public void calculateBMI() {
        if (height != null && weight != null && height > 0) {
            this.bmi = weight / ((height / 100) * (height / 100));
            this.bmi = Math.round(this.bmi * 10.0) / 10.0; // 四舍五入到一位小数
        }
    }

    public void assessHealthStatus() {
        StringBuilder status = new StringBuilder();

        if (bmi != null) {
            if (bmi < 18.5) {
                status.append("体重偏低 ");
            } else if (bmi >= 18.5 && bmi < 24.9) {
                status.append("体重正常 ");
            } else if (bmi >= 25 && bmi < 29.9) {
                status.append("超重 ");
            } else {
                status.append("肥胖 ");
            }
        }

        if (heartRate != null) {
            if (heartRate < 60) {
                status.append("心率偏低 ");
            } else if (heartRate > 100) {
                status.append("心率偏高 ");
            }
        }

        if (bloodOxygen != null && bloodOxygen < 95) {
            status.append("血氧偏低 ");
        }

        if (status.isEmpty()) {
            status.append("健康状况良好");
        }

        this.healthStatus = status.toString().trim();
    }
}
