package com.mole.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mole.health.pojo.AdminOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Cyanix-0721
 * @description 针对表【admin_operation_log(管理员操作日志表)】的数据库操作Mapper
 * @createDate 2024-10-15 15:40:59
 * @Entity com.mole.health.pojo.AdminOperationLog
 */
@Mapper
public interface AdminOperationLogMapper extends BaseMapper<AdminOperationLog> {

}




