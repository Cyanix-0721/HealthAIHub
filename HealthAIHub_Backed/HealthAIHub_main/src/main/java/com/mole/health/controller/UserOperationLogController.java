package com.mole.health.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mole.health.pojo.UserOperationLog;
import com.mole.health.result.CommonPage;
import com.mole.health.result.CommonResult;
import com.mole.health.service.UserOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户操作日志表控制器
 */
@RestController
@RequestMapping("/user/log")
public class UserOperationLogController {

    private final UserOperationLogService userOperationLogService;

    @Autowired
    public UserOperationLogController(UserOperationLogService userOperationLogService) {
        this.userOperationLogService = userOperationLogService;
    }

    /**
     * 获取所有用户操作日志
     */
    @GetMapping("/list")
    public CommonResult<CommonPage<UserOperationLog>> getAllUserOperationLogs(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        IPage<UserOperationLog> page = userOperationLogService.page(new Page<>(pageNum, pageSize));
        CommonPage<UserOperationLog> commonPage = CommonPage.restPage(page);
        return CommonResult.success(commonPage);
    }

    /**
     * 根据ID获取用户操作日志
     */
    @GetMapping("/{id}")
    public CommonResult<UserOperationLog> getUserOperationLogById(@PathVariable Integer id) {
        UserOperationLog userOperationLog = userOperationLogService.getById(id);
        return userOperationLog != null ? CommonResult.success(userOperationLog) : CommonResult.failed("UserOperationLog not found");
    }

    /**
     * 根据条件获取用户操作日志
     */
    @GetMapping("/condition")
    public CommonResult<CommonPage<UserOperationLog>> getUserOperationLogByCondition(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize, @RequestBody UserOperationLog userOperationLog) {
        IPage<UserOperationLog> page = userOperationLogService.page(new Page<>(pageNum, pageSize), new QueryWrapper<>(userOperationLog));
        CommonPage<UserOperationLog> commonPage = CommonPage.restPage(page);
        return CommonResult.success(commonPage);
    }

    /**
     * 添加用户操作日志
     */
    @PostMapping("/insert")
    public CommonResult<Boolean> insertUserOperationLog(@RequestBody UserOperationLog userOperationLog) {
        boolean result = userOperationLogService.save(userOperationLog);
        return result ? CommonResult.success(true) : CommonResult.failed("Failed to insert user operation log");
    }

    /**
     * 更新用户操作日志
     */
    @PutMapping("/update")
    public CommonResult<Boolean> updateUserOperationLog(@RequestBody UserOperationLog userOperationLog) {
        boolean result = userOperationLogService.saveOrUpdate(userOperationLog);
        return result ? CommonResult.success(true) : CommonResult.failed("Failed to update user operation log");
    }

    /**
     * 删除用户操作日志
     */
    @DeleteMapping("/{id}")
    public CommonResult<Boolean> deleteUserOperationLog(@PathVariable Integer id) {
        boolean result = userOperationLogService.removeById(id);
        return result ? CommonResult.success(true) : CommonResult.failed("Failed to delete user operation log");
    }
}