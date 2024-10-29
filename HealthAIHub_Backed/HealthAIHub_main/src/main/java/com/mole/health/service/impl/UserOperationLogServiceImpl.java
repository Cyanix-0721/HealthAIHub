package com.mole.health.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mole.health.mapper.UserOperationLogMapper;
import com.mole.health.pojo.UserOperationLog;
import com.mole.health.service.UserOperationLogService;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @description 针对表【user_operation_log(用户操作日志表)】的数据库操作Service实现
 * @createDate 2024-10-15 15:40:59
 */
@Service
public class UserOperationLogServiceImpl extends ServiceImpl<UserOperationLogMapper, UserOperationLog> implements UserOperationLogService {
}




