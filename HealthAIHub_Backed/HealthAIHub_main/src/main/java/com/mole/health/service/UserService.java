package com.mole.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mole.health.dto.user.*;
import com.mole.health.pojo.User;
import com.mole.health.result.CommonPage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2024-10-15 15:40:59
 */
public interface UserService extends IService<User> {

    /**
     * 注册用户
     *
     * @param userRegisterDto 用户注册数据传输对象
     * @return 注册成功的用户
     */
    String register(UserRegisterDto userRegisterDto);

    /**
     * 登录用户
     *
     * @param userLoginDto 用户登录数据传输对象
     * @return 登录成功的用户
     */
    String login(UserLoginDto userLoginDto);

    /**
     * 登出用户
     *
     * @return 登出成功
     */
    String logout();

    /**
     * 检查用户登录状态
     *
     * @return 用户是否已登录
     */
    boolean checkLoginStatus();

    /**
     * 验证验证码
     *
     * @param emailDto 包含邮箱和验证码的DTO
     * @return 验证结果
     */
    boolean verifyCode(EmailDto emailDto);

    /**
     * 获取用户信息
     *
     * @param username 用户名
     * @param email    邮箱
     * @return 用户信息
     */
    UserDto getUserInfo(String username, String email);

    /**
     * 重置密码
     *
     * @param resetPasswordDto 重置密码数据传输对象
     * @return 重置密码成功的用户
     */
    String resetPassword(ResetPasswordDto resetPasswordDto);

    /**
     * 更新用户信息
     *
     * @param userUpdateDto 用户更新数据传输对象
     * @return 更新后的用户信息
     */
    UserDto updateUserProfile(UserUpdateDto userUpdateDto);

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 获取用户列表，支持用户名模糊搜索
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param username 用户名（可选，用于模糊搜索）
     * @return 用户列表
     */
    CommonPage<User> getUserList(int pageNum, int pageSize, String username);

    /**
     * 批量添加或更新用户
     *
     * @param userSaveDtoList 用户存储数据传输对象列表
     * @return 是否操作成功
     */
    boolean batchSaveOrUpdateUsers(List<UserSaveDto> userSaveDtoList);

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteUsers(List<Integer> ids);
}
