package com.mole.health.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mole.health.mapper.AdminOperationLogMapper;
import com.mole.health.pojo.AdminOperationLog;
import com.mole.health.service.AdminOperationLogService;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @description 针对表【admin_operation_log(管理员操作日志表)】的数据库操作Service实现
 * @createDate 2024-10-15 15:40:59
 */
@Service
public class AdminOperationLogServiceImpl extends ServiceImpl<AdminOperationLogMapper, AdminOperationLog>
        implements AdminOperationLogService {
}




