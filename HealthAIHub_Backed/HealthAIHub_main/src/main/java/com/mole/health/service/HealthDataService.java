package com.mole.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mole.health.dto.health.HealthDataDto;
import com.mole.health.dto.health.HealthDataSaveDto;
import com.mole.health.pojo.HealthData;

/**
 * @author Administrator
 * @description 针对表【health_data(健康数据表)】的数据库操作Service
 * @createDate 2024-10-15 15:40:59
 */
public interface HealthDataService extends IService<HealthData> {
    /**
     * 获取用户最新的健康数据
     *
     * @param userId 用户ID
     * @return 最新的健康数据记录
     */
    HealthData getLatestByUserId(String userId);

    /*
     * 将健康数据转换为DTO对象
     * @param healthData 健康数据对象
     * @return HealthDataDto 健康数据DTO对象
     */
    HealthDataDto convertToDto(HealthData healthData);

    /**
     * 将UserHealthDataSaveDto转换为HealthData对象
     *
     * @param dto UserHealthDataSaveDto
     * @return HealthData 健康数据对象
     */
    HealthData convertToHealthData(HealthDataSaveDto dto);
}
