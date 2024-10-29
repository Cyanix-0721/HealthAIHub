package com.mole.health.controller;

import com.mole.health.dto.user.*;
import com.mole.health.result.CommonResult;
import com.mole.health.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员授权控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    private final AdminService adminService;

    /**
     * 构造函数，通过依赖注入初始化AdminService
     *
     * @param adminService 管理员服务接口
     */
    @Autowired
    public AdminAuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 管理员注册接口
     *
     * @param adminRegisterDto 包含管理员注册信息的数据传输对象
     * @return 包含注册成功的令牌的通用结果对象
     */
    @PostMapping("/register")
    @Operation(summary = "管理员注册", description = "注册一个新管理员")
    @ApiResponse(responseCode = "200", description = "注册成功")
    public CommonResult<String> register(@RequestBody @Parameter(description = "管理员数据传输对象") AdminRegisterDto adminRegisterDto) {
        String token = adminService.register(adminRegisterDto);
        return CommonResult.success(token);
    }

    /**
     * 管理员登录接口
     *
     * @param adminLoginDto 包含管理员登录信息的数据传输对象
     * @return 包含登录成功后的令牌的通用结果对象
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "管理员登录并获取令牌")
    @ApiResponse(responseCode = "200", description = "登录成功")
    public CommonResult<String> login(@RequestBody @Parameter(description = "管理员数据传输对象") AdminLoginDto adminLoginDto) {
        String token = adminService.login(adminLoginDto);
        return CommonResult.success(token);
    }

    /**
     * 管理员登出接口
     *
     * @return 登出成功的消息
     */
    @PostMapping("/logout")
    @Operation(summary = "管理员登出", description = "管理员登出并删除令牌")
    @ApiResponse(responseCode = "200", description = "登出成功")
    public CommonResult<String> logout() {
        String result = adminService.logout();
        return CommonResult.success(result);
    }

    /**
     * 检查登录状态
     *
     * @return 登录状态
     */
    @GetMapping("/check-login-status")
    @Operation(summary = "检查登录状态", description = "检查当前管理员是否已登录")
    @ApiResponse(responseCode = "200", description = "成功返回登录状态")
    public CommonResult<Boolean> checkLoginStatus() {
        boolean isLoggedIn = adminService.checkLoginStatus();
        return CommonResult.success(isLoggedIn);
    }

    /**
     * 获取管理员信息
     *
     * @param username 管理员名
     * @param email    邮箱
     * @return 管理员信息
     */
    @GetMapping("/get-admin-info")
    @Operation(summary = "获取管理员信息", description = "根据管理员名或邮箱获取指定管理员的信息")
    @ApiResponse(responseCode = "200", description = "成功返回管理员信息")
    public CommonResult<AdminDto> getUserInfo(@RequestParam(required = false) @Parameter(description = "管理员名") String username, @RequestParam(required = false) @Parameter(description = "邮箱") String email) {
        AdminDto adminDto = adminService.getAdminInfo(username, email);
        return CommonResult.success(adminDto);
    }

    /**
     * 重置密码
     *
     * @param resetPasswordDto 重置密码数据传输对象
     * @return 重置密码成功的消息
     */
    @PostMapping("/reset-password")
    @Operation(summary = "重置密码", description = "通过邮箱验证码重置管理员密码")
    @ApiResponse(responseCode = "200", description = "密码重置成功")
    public CommonResult<String> resetPassword(@RequestBody @Parameter(description = "重置密码数据传输对象") ResetPasswordDto resetPasswordDto) {
        String result = adminService.resetPassword(resetPasswordDto);
        return CommonResult.success(result);
    }

    /**
     * 更新管理员信息
     *
     * @param adminUpdateDto 管理员更新数据传输对象
     * @return 更新后的管理员信息
     */
    @PostMapping("/update-admin-profile")
    @Operation(summary = "更新管理员信息", description = "更新指定管理员的信息")
    @ApiResponse(responseCode = "200", description = "更新成功")
    public CommonResult<AdminDto> updateUserProfile(@RequestBody @Parameter(description = "管理员数据传输对象") AdminUpdateDto adminUpdateDto) {
        AdminDto updatedUser = adminService.updateAdminProfile(adminUpdateDto);
        return CommonResult.success(updatedUser);
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    @PostMapping("/upload-avatar")
    @Operation(summary = "上传头像", description = "上传用户头像")
    @ApiResponse(responseCode = "200", description = "上传成功")
    public CommonResult<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = adminService.uploadAvatar(file);
        return CommonResult.success(avatarUrl);
    }
}
