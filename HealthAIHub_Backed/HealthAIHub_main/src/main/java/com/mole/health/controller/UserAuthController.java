package com.mole.health.controller;

import com.mole.health.dto.user.*;
import com.mole.health.result.CommonResult;
import com.mole.health.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户授权管理控制器 处理用户相关的HTTP请求，包括注册和登录
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "UserController", description = "用户管理控制器")
public class UserAuthController {

    private final UserService userService;

    /**
     * 构造函数，通过依赖注入初始化UserService
     *
     * @param userService 用户服务接口
     */
    @Autowired
    public UserAuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册接口
     *
     * @param userRegisterDto 包含用户注册信息的数据传输对象
     * @return 包含注册成功的令牌的通用结果对象
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册一个新用户")
    @ApiResponse(responseCode = "200", description = "注册成功")
    public CommonResult<String> register(@RequestBody @Parameter(description = "用户数据传输对象") UserRegisterDto userRegisterDto) {
        String token = userService.register(userRegisterDto);
        return CommonResult.success(token);
    }

    /**
     * 用户登录接口
     *
     * @param userLoginDto 包含用户登录信息的数据传输对象
     * @return 包含登录成功后的令牌的通用结果对象
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录并获取令牌")
    @ApiResponse(responseCode = "200", description = "登录成功")
    public CommonResult<String> login(@RequestBody @Parameter(description = "用户数据传输对象") UserLoginDto userLoginDto) {
        String token = userService.login(userLoginDto);
        return CommonResult.success(token);
    }

    /**
     * 用户登出接口
     *
     * @return 登出成功的消息
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出并删除令牌")
    @ApiResponse(responseCode = "200", description = "登出成功")
    public CommonResult<String> logout() {
        String result = userService.logout();
        return CommonResult.success(result);
    }

    /**
     * 检查登录状态
     *
     * @return 登录状态
     */
    @GetMapping("/check-login-status")
    @Operation(summary = "检查登录状态", description = "检查当前用户是否已登录")
    @ApiResponse(responseCode = "200", description = "成功返回登录状态")
    public CommonResult<Boolean> checkLoginStatus() {
        boolean isLoggedIn = userService.checkLoginStatus();
        return CommonResult.success(isLoggedIn);
    }

    /**
     * 获取用户信息
     *
     * @param username 用户名
     * @param email    邮箱
     * @return 用户信息
     */
    @GetMapping("/get-user-info")
    @Operation(summary = "获取用户信息", description = "根据用户名或邮箱获取指定用户的信息")
    @ApiResponse(responseCode = "200", description = "成功返回用户信息")
    public CommonResult<UserDto> getUserInfo(
            @RequestParam(required = false) @Parameter(description = "用户名") String username,
            @RequestParam(required = false) @Parameter(description = "邮箱") String email) {
        UserDto userInfo = userService.getUserInfo(username, email);
        return CommonResult.success(userInfo);
    }

    /**
     * 重置密码
     *
     * @param resetPasswordDto 重置密码数据传输对象
     * @return 重置密码成功的消息
     */
    @PostMapping("/reset-password")
    @Operation(summary = "重置密码", description = "通过邮箱验证码重置用户密码")
    @ApiResponse(responseCode = "200", description = "密码重置成功")
    public CommonResult<String> resetPassword(@RequestBody @Parameter(description = "重置密码数据传输对象") ResetPasswordDto resetPasswordDto) {
        String result = userService.resetPassword(resetPasswordDto);
        return CommonResult.success(result);
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateDto 用户更新数据传输对象
     * @return 更新后的用户信息
     */
    @PostMapping("/update-user-profile")
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    @ApiResponse(responseCode = "200", description = "更新成功")
    public CommonResult<UserDto> updateUserProfile(@RequestBody @Parameter(description = "用户数据传输对象") UserUpdateDto userUpdateDto) {
        UserDto updatedUser = userService.updateUserProfile(userUpdateDto);
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
        String avatarUrl = userService.uploadAvatar(file);
        return CommonResult.success(avatarUrl);
    }
}
