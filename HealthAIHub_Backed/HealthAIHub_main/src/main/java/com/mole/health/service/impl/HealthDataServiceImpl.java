package com.mole.health.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mole.health.dto.health.HealthDataDto;
import com.mole.health.dto.health.HealthDataSaveDto;
import com.mole.health.mapper.HealthDataMapper;
import com.mole.health.pojo.HealthData;
import com.mole.health.service.HealthDataService;

/**
 * @author Administrator
 * @description 针对表【health_data(健康数据表)】的数据库操作Service实现
 * @createDate 2024-10-15 15:40:59
 */
@Service
public class HealthDataServiceImpl extends ServiceImpl<HealthDataMapper, HealthData> implements HealthDataService {
    @Override
    public HealthData getLatestByUserId(String userId) {
        LambdaQueryWrapper<HealthData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HealthData::getUserId, userId)
                   .orderByDesc(HealthData::getUpdatedAt)
                   .last("LIMIT 1");
        return getOne(queryWrapper);
    }

    @Override
    public HealthDataDto convertToDto(HealthData healthData) {
        HealthDataDto dto = new HealthDataDto();
        BeanUtils.copyProperties(healthData, dto);
        dto.calculateBMI();
        dto.assessHealthStatus();
        return dto;
    }

    @Override
    public HealthData convertToHealthData(HealthDataSaveDto dto) {
        HealthData healthData = new HealthData();
        BeanUtils.copyProperties(dto, healthData);
        return healthData;
    }
}
