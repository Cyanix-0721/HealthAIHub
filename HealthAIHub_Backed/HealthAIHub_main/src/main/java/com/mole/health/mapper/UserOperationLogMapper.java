package com.mole.health.mapper;

import com.mole.health.pojo.UserOperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Cyanix-0721
* @description 针对表【user_operation_log(用户操作日志表)】的数据库操作Mapper
* @createDate 2024-10-15 15:40:59
* @Entity com.mole.health.pojo.UserOperationLog
*/
@Mapper
public interface UserOperationLogMapper extends BaseMapper<UserOperationLog> {

}




