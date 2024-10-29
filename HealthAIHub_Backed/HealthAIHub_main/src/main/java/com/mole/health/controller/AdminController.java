package com.mole.health.controller;

import com.mole.health.dto.user.AdminSaveDto;
import com.mole.health.pojo.Admin;
import com.mole.health.result.CommonPage;
import com.mole.health.result.CommonResult;
import com.mole.health.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * 构造函数，通过依赖注入初始化AdminService
     *
     * @param adminService 管理员服务接口
     */
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 获取管理员列表，支持用户名模糊搜索
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param username 用户名（可选，用于模糊搜索）
     * @return 管理员列表
     */
    @GetMapping("/list")
    public CommonResult<CommonPage<Admin>> getAdminList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username) {
        CommonPage<Admin> result = adminService.getAdminList(pageNum, pageSize, username);
        return CommonResult.success(result);
    }

    /**
     * 批量添加或更新管理员
     *
     * @param adminSaveDtoList 管理员存储数据传输对象列表
     * @return 是否操作成功
     */
    @PostMapping("/batch")
    public CommonResult<Boolean> batchSaveOrUpdateAdmins(@RequestBody List<AdminSaveDto> adminSaveDtoList) {
        boolean result = adminService.batchSaveOrUpdateAdmins(adminSaveDtoList);
        return result ? CommonResult.success(true) : CommonResult.failed("批量添加或更新管理员失败");
    }

    /**
     * 批量删除管理员
     *
     * @param ids 管理员ID列表
     * @return 是否删除成功
     */
    @DeleteMapping("/batch")
    public CommonResult<Boolean> batchDeleteAdmins(@RequestBody List<Integer> ids) {
        boolean result = adminService.batchDeleteAdmins(ids);
        return result ? CommonResult.success(true) : CommonResult.failed("批量删除管理员失败");
    }

}
