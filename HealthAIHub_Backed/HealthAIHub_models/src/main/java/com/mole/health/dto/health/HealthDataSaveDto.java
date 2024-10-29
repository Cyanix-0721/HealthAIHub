package com.mole.health.dto.health;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "HealthDataSaveDto", description = "健康数据存储传输对象")
public class HealthDataSaveDto {

    @NotNull(message = "身高不能为空")
    @Min(value = 50, message = "身高不能小于50cm")
    @Max(value = 300, message = "身高不能大于300cm")
    @Schema(description = "身高(cm)")
    private Double height;

    @NotNull(message = "体重不能为空")
    @Min(value = 2, message = "体重不能小于2kg")
    @Max(value = 500, message = "体重不能大于500kg")
    @Schema(description = "体重(kg)")
    private Double weight;

    @NotNull(message = "心率不能为空")
    @Min(value = 30, message = "心率不能小于30次/分")
    @Max(value = 220, message = "心率不能大于220次/分")
    @Schema(description = "心率(次/分)")
    private Integer heartRate;

    @NotNull(message = "血压不能为空")
    @Pattern(regexp = "^\\d{2,3}/\\d{2,3}$", message = "血压格式不正确，应为xxx/xxx")
    @Schema(description = "血压(mmHg)")
    private String bloodPressure;

    @NotNull(message = "血氧饱和度不能为空")
    @Min(value = 50, message = "血氧饱和度不能小于50%")
    @Max(value = 100, message = "血氧饱和度不能大于100%")
    @Schema(description = "血氧饱和度(%)")
    private Integer bloodOxygen;

    @NotNull(message = "血糖不能为空")
    @Min(value = 1, message = "血糖不能小于1mmol/L")
    @Max(value = 30, message = "血糖不能大于30mmol/L")
    @Schema(description = "血糖(mmol/L)")
    private Double bloodSugar;
}
