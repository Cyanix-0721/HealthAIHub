package com.mole.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mole.health.dto.user.*;
import com.mole.health.pojo.Admin;
import com.mole.health.result.CommonPage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【admin(管理员表)】的数据库操作Service
 * @createDate 2024-10-15 15:40:59
 */
public interface AdminService extends IService<Admin> {

    /**
     * 注册管理员
     *
     * @param adminRegisterDto 管理员注册数据传输对象
     * @return 注册成功的管理员
     */
    String register(AdminRegisterDto adminRegisterDto);

    /**
     * 登录管理员
     *
     * @param adminLoginDto 管理员登录数据传输对象
     * @return 登录成功的管理员
     */
    String login(AdminLoginDto adminLoginDto);

    /**
     * 登出管理员
     *
     * @return 登出成功
     */
    String logout();

    /**
     * 检查管理员登录状态
     *
     * @return 管理员是否已登录
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
     * 获取管理员信息
     *
     * @param username 管理员名
     * @param email    邮箱
     * @return 管理员信息
     */
    AdminDto getAdminInfo(String username, String email);

    /**
     * 重置密码
     *
     * @param resetPasswordDto 重置密码数据传输对象
     * @return 重置密码成功的管理员
     */
    String resetPassword(ResetPasswordDto resetPasswordDto);

    /**
     * 更新管理员信息
     *
     * @param adminUpdateDto 管理员更新数据传输对象
     * @return 更新后的管理员信息
     */
    AdminDto updateAdminProfile(AdminUpdateDto adminUpdateDto);

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 获取管理员列表，支持用户名模糊搜索
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param username 用户名（可选，用于模糊搜索）
     * @return 管理员列表
     */
    CommonPage<Admin> getAdminList(int pageNum, int pageSize, String username);

    /**
     * 批量添加或更新管理员
     *
     * @param adminSaveDtoList 管理员存储数据传输对象列表
     * @return 是否操作成功
     */
    boolean batchSaveOrUpdateAdmins(List<AdminSaveDto> adminSaveDtoList);

    /**
     * 批量删除管理员
     *
     * @param ids 管理员ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteAdmins(List<Integer> ids);
}
