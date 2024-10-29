package com.mole.health.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mole.health.pojo.AdminOperationLog;
import com.mole.health.result.CommonPage;
import com.mole.health.result.CommonResult;
import com.mole.health.service.AdminOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员操作日志表控制器
 */
@RestController
@RequestMapping("/admin/log")
public class AdminOperationLogController {

    @Autowired
    private AdminOperationLogService adminOperationLogService;

    /**
     * 获取所有管理员操作日志
     */
    @GetMapping("/list")
    public CommonResult<CommonPage<AdminOperationLog>> getAllAdminOperationLogs(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        IPage<AdminOperationLog> page = adminOperationLogService.page(new Page<>(pageNum, pageSize));
        CommonPage<AdminOperationLog> commonPage = CommonPage.restPage(page);
        return CommonResult.success(commonPage);
    }

    /**
     * 根据ID获取管理员操作日志
     */
    @GetMapping("/{id}")
    public CommonResult<AdminOperationLog> getAdminOperationLogById(@PathVariable Integer id) {
        AdminOperationLog adminOperationLog = adminOperationLogService.getById(id);
        return adminOperationLog != null ? CommonResult.success(adminOperationLog) : CommonResult.failed("AdminOperationLog not found");
    }

    /**
     * 根据条件获取管理员操作日志
     */
    @GetMapping("/condition")
    public CommonResult<CommonPage<AdminOperationLog>> getAdminOperationLogByCondition(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize, @RequestBody AdminOperationLog adminOperationLog) {
        IPage<AdminOperationLog> page = adminOperationLogService.page(new Page<>(pageNum, pageSize), new QueryWrapper<>(adminOperationLog));
        CommonPage<AdminOperationLog> commonPage = CommonPage.restPage(page);
        return CommonResult.success(commonPage);
    }

    /**
     * 添加管理员操作日志
     */
    @PostMapping("/insert")
    public CommonResult<Boolean> insertAdminOperationLog(@RequestBody AdminOperationLog adminOperationLog) {
        boolean result = adminOperationLogService.save(adminOperationLog);
        return result ? CommonResult.success(true) : CommonResult.failed("Failed to insert admin operation log");
    }

    /**
     * 更新管理员操作日志
     */
    @PutMapping("/update")
    public CommonResult<Boolean> updateAdminOperationLog(@RequestBody AdminOperationLog adminOperationLog) {
        boolean result = adminOperationLogService.saveOrUpdate(adminOperationLog);
        return result ? CommonResult.success(true) : CommonResult.failed("Failed to update admin operation log");
    }

    /**
     * 删除管理员操作日志
     */
    @DeleteMapping("/{id}")
    public CommonResult<Boolean> deleteAdminOperationLog(@PathVariable Integer id) {
        boolean result = adminOperationLogService.removeById(id);
        return result ? CommonResult.success(true) : CommonResult.failed("Failed to delete admin operation log");
    }
}