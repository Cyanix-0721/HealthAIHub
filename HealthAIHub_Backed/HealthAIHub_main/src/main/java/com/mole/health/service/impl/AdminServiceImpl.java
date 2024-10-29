package com.mole.health.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mole.health.dto.user.*;
import com.mole.health.exception.Asserts;
import com.mole.health.exception.BusinessException;
import com.mole.health.mapper.AdminMapper;
import com.mole.health.pojo.Admin;
import com.mole.health.properties.MinioProperties;
import com.mole.health.result.CommonPage;
import com.mole.health.result.ResultCode;
import com.mole.health.service.AdminService;
import com.mole.health.service.EmailVerificationCodeService;
import com.mole.health.util.MinioUtil;
import com.mole.health.util.StpKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【admin(管理员表)】的数据库操作Service实现
 * @createDate 2024-10-15 15:40:59
 */
@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final EmailVerificationCodeService emailVerificationCodeService;
    private final MinioUtil minioUtil;
    private final MinioProperties minioProperties;

    @Autowired
    public AdminServiceImpl(EmailVerificationCodeService emailVerificationCodeService, MinioUtil minioUtil, MinioProperties minioProperties) {
        this.emailVerificationCodeService = emailVerificationCodeService;
        this.minioUtil = minioUtil;
        this.minioProperties = minioProperties;
    }

    @Override
    public String register(AdminRegisterDto adminRegisterDto) {
        validateRegistrationData(adminRegisterDto);

        // 验证验证码
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail(adminRegisterDto.getEmail());
        emailDto.setEmailVerificationCode(adminRegisterDto.getEmailVerificationCode());
        if (!verifyCode(emailDto)) {
            Asserts.failBusiness("验证码错误或已过期");
        }

        Admin admin = BeanUtil.copyProperties(adminRegisterDto, Admin.class);
        admin.setPassword(BCrypt.hashpw(admin.getPassword()));
        admin.setCreatedAt(LocalDateTime.now());

        if (!this.save(admin)) {
            Asserts.failBusiness("注册失败，请稍后重试");
        }

        // 注册成功后直接登录
        StpKit.ADMIN.login(admin.getId());
        return StpKit.ADMIN.getTokenValue();
    }

    /**
     * 用户登录
     *
     * @param adminLoginDto 包含登录信息的数据传输对象
     * @return 登录成功后的令牌
     * @throws com.mole.health.exception.BusinessException 当登录失败时抛出业务异常
     */
    @Override
    public String login(AdminLoginDto adminLoginDto) {
        // 首先检查用户是否已经登录
        if (checkLoginStatus()) {
            Asserts.failBusiness("用户已登录，请勿重复登录");
        }

        // 验证登录数据并获取用户信息
        Admin admin = validateLoginData(adminLoginDto);

        // 根据登录类型进行验证
        switch (adminLoginDto.getLoginType()) {
            case USERNAME, EMAIL -> {
                // 验证密码
                if (StrUtil.isBlank(adminLoginDto.getPassword()) || !BCrypt.checkpw(adminLoginDto.getPassword(), admin.getPassword())) {
                    Asserts.failBusiness("密码错误");
                }
            }
            case EMAIL_VERIFICATION_CODE -> {
                // 验证邮箱验证码
                EmailDto emailDto = new EmailDto();
                emailDto.setEmail(adminLoginDto.getEmail());
                emailDto.setEmailVerificationCode(adminLoginDto.getEmailVerificationCode());
                if (!verifyCode(emailDto)) {
                    Asserts.failBusiness("验证码错误或已过期");
                }
            }
            default -> Asserts.failBusiness("不支持的登录类型");
        }

        try {
            // 执行登录操作
            StpKit.ADMIN.login(admin.getId(), "admin");
            // 返回登录令牌
            return StpKit.ADMIN.getTokenValue();
        } catch (Exception e) {
            Asserts.failBusiness("登录失败，请稍后重试");
        }
        return null;
    }

    /**
     * 登出用户
     *
     * @return 登出成功的消息
     */
    @Override
    public String logout() {
        try {
            // 检查用户是否已登录
            if (!checkLoginStatus()) {
                return "用户未登录";
            }

            // 执行登出操作，指定登录类型
            StpKit.ADMIN.logout(StpKit.ADMIN.getLoginIdAsLong(), "admin");
            return "登出成功";
        } catch (Exception e) {
            Asserts.failBusiness("登出失败，请稍后重试");
        }
        return null;
    }

    private void validateRegistrationData(AdminRegisterDto adminRegisterDto) {
        if (adminRegisterDto == null) {
            Asserts.failBusiness("注册信息不能为空");
        }
        if (StrUtil.isBlank(adminRegisterDto.getUsername())) {
            Asserts.failBusiness("用户名不能为空");
        }
        if (StrUtil.isBlank(adminRegisterDto.getPassword())) {
            Asserts.failBusiness("密码不能为空");
        }
        if (StrUtil.isBlank(adminRegisterDto.getEmail())) {
            Asserts.failBusiness("邮箱不能为空");
        }
        if (StrUtil.isBlank(adminRegisterDto.getEmailVerificationCode())) {
            Asserts.failBusiness("验证码不能为空");
        }

        if (this.getOne(new QueryWrapper<Admin>().eq("username", adminRegisterDto.getUsername())) != null) {
            Asserts.failBusiness("用户名已存在", ResultCode.VALIDATE_FAILED);
        }

        if (this.getOne(new QueryWrapper<Admin>().eq("email", adminRegisterDto.getEmail())) != null) {
            Asserts.failBusiness("邮箱已被使用");
        }
    }

    /**
     * 验证登录数据
     *
     * @param adminLoginDto 包含登录信息的数据传输对象
     * @return 验证成功后的用户对象
     * @throws com.mole.health.exception.BusinessException 当验证失败时抛出业异常
     */
    private Admin validateLoginData(AdminLoginDto adminLoginDto) {
        // 检查登录信息是否为空
        if (ObjectUtil.isNull(adminLoginDto)) {
            Asserts.failBusiness("登录信息不能为空");
        }

        Admin admin = null;
        switch (adminLoginDto.getLoginType()) {
            case USERNAME -> {
                if (StrUtil.isBlank(adminLoginDto.getUsername())) {
                    Asserts.failBusiness("用户名不能为空");
                }
                admin = this.getOne(new QueryWrapper<Admin>().eq("username", adminLoginDto.getUsername()));
                if (admin == null) {
                    Asserts.failBusiness("用户名不存在", ResultCode.UNAUTHORIZED);
                }
            }
            case EMAIL, EMAIL_VERIFICATION_CODE -> {
                if (StrUtil.isBlank(adminLoginDto.getEmail())) {
                    Asserts.failBusiness("邮箱不能为空");
                }
                admin = this.getOne(new QueryWrapper<Admin>().eq("email", adminLoginDto.getEmail()));
                if (admin == null) {
                    Asserts.failBusiness("邮箱不存在", ResultCode.UNAUTHORIZED);
                }
            }
            default -> Asserts.failBusiness("不支持的登录类型");
        }

        return admin;
    }

    /**
     * 检查用户登录状态
     *
     * @return 用户是否已登录
     */
    @Override
    public boolean checkLoginStatus() {
        // 检查特定类型的登录状态
        return StpKit.ADMIN.isLogin("admin");
    }

    @Override
    public boolean verifyCode(EmailDto emailDto) {
        return emailVerificationCodeService.verifyCode(emailDto);
    }

    /**
     * 获取用户信息
     *
     * @param username 用户名
     * @param email    邮箱
     * @return 用户信息
     */
    @Override
    public AdminDto getAdminInfo(String username, String email) {
        if (StrUtil.isBlank(username) && StrUtil.isBlank(email)) {
            Asserts.failBusiness("用户名和邮箱不能同时为空");
        }

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(username)) {
            queryWrapper.eq("username", username);
        } else {
            queryWrapper.eq("email", email);
        }

        Admin admin = this.getOne(queryWrapper);
        if (admin == null) {
            Asserts.failBusiness("用户不存在");
        }

        return convertToAdminDto(admin);
    }

    /**
     * 将Admin对象转换为AdminDto对象
     *
     * @param admin 用户对象
     * @return AdminDto对象
     */
    private AdminDto convertToAdminDto(Admin admin) {
        AdminDto adminDto = new AdminDto();
        BeanUtil.copyProperties(admin, adminDto, "password");
        return adminDto;
    }

    /*
     * 重置密码
     *
     * @param resetPasswordDto 重置密码数据传输对象
     * @return 重置密码成功的消息
     */
    @Override
    public String resetPassword(ResetPasswordDto resetPasswordDto) {
        // 验证邮箱验证码
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail(resetPasswordDto.getEmail());
        emailDto.setEmailVerificationCode(resetPasswordDto.getEmailVerificationCode());
        if (!verifyCode(emailDto)) {
            Asserts.failBusiness("验证码错误或已过期");
        }

        // 查找用户
        Admin admin = this.getOne(new QueryWrapper<Admin>().eq("email", resetPasswordDto.getEmail()));
        if (admin == null) {
            Asserts.failBusiness("用户不存在");
        }

        // 更新密码
        admin.setPassword(BCrypt.hashpw(resetPasswordDto.getNewPassword()));
        if (!this.updateById(admin)) {
            Asserts.failBusiness("密码重置失败，请稍后重试");
        }

        return "密码重置成功";
    }

    /**
     * 更新用户信息
     *
     * @param adminUpdateDto 用户更新数据传输对象
     * @return 更新后的用户信息
     */
    @Override
    public AdminDto updateAdminProfile(AdminUpdateDto adminUpdateDto) {
        Long adminId = null;
        try {
            adminId = StpKit.ADMIN.getLoginIdAsLong();
            System.out.println("成功获取到用户ID: " + adminId);
        } catch (Exception e) {
            System.out.println("获取用户ID失败: " + e.getMessage());
        }
        Admin admin = this.getById(adminId);
        if (admin == null) {
            Asserts.failBusiness("用户不存在", ResultCode.VALIDATE_FAILED);
        }

        // 更新邮箱
        if (StrUtil.isNotBlank(adminUpdateDto.getEmail()) && !adminUpdateDto.getEmail().equals(admin.getEmail())) {
            // 检查新邮箱是否已使用
            if (this.getOne(new QueryWrapper<Admin>().eq("email", adminUpdateDto.getEmail())) != null) {
                Asserts.failBusiness("该邮箱已被使用", ResultCode.VALIDATE_FAILED);
            }

            // 验证邮箱验证码
            if (StrUtil.isBlank(adminUpdateDto.getEmailVerificationCode())) {
                Asserts.failBusiness("更新邮箱需要验证码", ResultCode.VALIDATE_FAILED);
            }
            EmailDto emailDto = new EmailDto();
            emailDto.setEmail(adminUpdateDto.getEmail());
            emailDto.setEmailVerificationCode(adminUpdateDto.getEmailVerificationCode());
            System.out.println("emailDto: " + emailDto);
            if (!verifyCode(emailDto)) {
                Asserts.failBusiness("邮箱验证码错误或已过期", ResultCode.VALIDATE_FAILED);
            }

            admin.setEmail(adminUpdateDto.getEmail());
        }

        // 更新密码
        if (StrUtil.isNotBlank(adminUpdateDto.getOldPassword()) && StrUtil.isNotBlank(adminUpdateDto.getNewPassword())) {
            if (!BCrypt.checkpw(adminUpdateDto.getOldPassword(), admin.getPassword())) {
                Asserts.failBusiness("旧密码不正确", ResultCode.VALIDATE_FAILED);
            }
            admin.setPassword(BCrypt.hashpw(adminUpdateDto.getNewPassword()));
        }

        if (!this.updateById(admin)) {
            Asserts.failBusiness("更新用户信息失败");
        }

        return convertToAdminDto(admin);
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    @Transactional
    @Override
    public String uploadAvatar(MultipartFile file) {
        String bucketName = minioProperties.getBucketName();
        try {
            log.info("开始上传头像，文件大小: {} bytes", file.getSize());

            // 检查文件大小
            long MAX_FILE_SIZE = 5 * 1024 * 1024;
            if (file.getSize() > MAX_FILE_SIZE) {
                Asserts.failBusiness("文件大小超过限制，最大允许5MB", ResultCode.VALIDATE_FAILED);
            }

            // 获取当前登录用户的ID
            Long adminId = StpKit.ADMIN.getLoginIdAsLong();
            Admin admin = this.getById(adminId);
            if (admin == null) {
                Asserts.failBusiness("用户不存在", ResultCode.UNAUTHORIZED);
            }

            // 检查存储桶是否存在，如果不存在则创建
            if (!minioUtil.bucketExists(bucketName)) {
                minioUtil.createBucket(bucketName);
                minioUtil.setBucketPolicy(bucketName);
            }

            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

            // 生成唯一的文件名
            String fileName = "avatar_admin_" + adminId + "_" + System.currentTimeMillis() + extension;

            // 上传文件到Minio
            minioUtil.uploadFileWithFileName(bucketName, fileName, file);

            // 只存储相对路径
            String avatarPath = bucketName + "/" + fileName;
            admin.setAvatar(avatarPath);
            boolean updateSuccess = this.updateById(admin);
            if (!updateSuccess) {
                log.error("Failed to update user avatar in database");
                throw new BusinessException("更新用户头像失败");
            }

            String fullUrl = getFullAvatarUrl(avatarPath);
            log.info("头像上传成功，URL: {}", fullUrl);
            return fullUrl;  // 返回完整的 URL
        } catch (DataIntegrityViolationException e) {
            log.error("数据库更新失败，可能是字段长度不足", e);
            throw new BusinessException("头像 URL 太长，无法保存");
        } catch (IOException e) {
            log.error("读取文件流失败", e);
            Asserts.failBusiness("上传头像失败: 无法读取文件");
        } catch (Exception e) {
            log.error("上传头像失败", e);
            Asserts.failBusiness("上传头像失败: " + e.getMessage());
        }
        return null;
    }

    /*
     * 获取完整的头像 URL
     *
     * @param avatarPath 头像路径
     */
    private String getFullAvatarUrl(String avatarPath) {
        try {
            String[] parts = avatarPath.split("/", 2);
            if (parts.length == 2) {
                return minioUtil.getFileUrl(parts[0], parts[1]);
            } else {
                log.error("Invalid avatar path format: {}", avatarPath);
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to get avatar URL", e);
            return null;
        }
    }

    /**
     * 获取管理员列表，支持用户名模糊搜索
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param username 用户名（可选，用于模糊搜索）
     * @return 管理员列表
     */

    @Override
    public CommonPage<Admin> getAdminList(int pageNum, int pageSize, String username) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            queryWrapper.like(Admin::getUsername, username);
        }

        IPage<Admin> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        return CommonPage.restPage(page);
    }

    /**
     * 批量添加或更新管理员
     *
     * @param adminSaveDtoList 管理员存储数据传输对象列表
     * @return 是否操作成功
     */

    @Override
    @Transactional
    public boolean batchSaveOrUpdateAdmins(List<AdminSaveDto> adminSaveDtoList) {
        List<Admin> adminList = adminSaveDtoList.stream().map(dto -> {
            Admin admin;
            if (dto.getId() != null) {
                log.info("更新现有管理员，ID: {}", dto.getId());
                // 更新现有管理员
                admin = this.getById(dto.getId());
                if (admin == null) {
                    log.error("管理员不存在，ID: {}", dto.getId());
                    Asserts.failBusiness("管理员不存在，ID: " + dto.getId());
                }
                admin.setUsername(dto.getUsername());
                admin.setEmail(dto.getEmail());
                if (StrUtil.isNotBlank(dto.getPassword())) {
                    // 只有在提供了新密码时才更新密码
                    admin.setPassword(BCrypt.hashpw(dto.getPassword()));
                }
            } else {
                log.info("新增管理员，用户名: {}", dto.getUsername());
                // 新增管理员
                admin = new Admin();
                admin.setUsername(dto.getUsername());
                admin.setEmail(dto.getEmail());
                if (StrUtil.isBlank(dto.getPassword())) {
                    throw new RuntimeException("新管理员密码不能为空");
                }
                admin.setPassword(BCrypt.hashpw(dto.getPassword()));
            }
            return admin;
        }).collect(Collectors.toList());

        return this.saveOrUpdateBatch(adminList);
    }

    /**
     * 批量删除管理员
     *
     * @param ids 管理员ID列表
     * @return 是否删除成功
     */

    @Override
    public boolean batchDeleteAdmins(List<Integer> ids) {
        return this.removeByIds(ids);
    }

}
