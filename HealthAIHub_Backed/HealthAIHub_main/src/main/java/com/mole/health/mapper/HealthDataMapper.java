package com.mole.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mole.health.pojo.HealthData;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Cyanix-0721
 * @description 针对表【health_data(健康数据表)】的数据库操作Mapper
 * @createDate 2024-10-15 15:40:59
 * @Entity com.mole.health.pojo.HealthData
 */
@Mapper
public interface HealthDataMapper extends BaseMapper<HealthData> {

}




