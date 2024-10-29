package com.mole.health.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mole.health.dto.user.*;
import com.mole.health.exception.Asserts;
import com.mole.health.exception.BusinessException;
import com.mole.health.mapper.UserMapper;
import com.mole.health.pojo.User;
import com.mole.health.properties.MinioProperties;
import com.mole.health.result.CommonPage;
import com.mole.health.result.ResultCode;
import com.mole.health.service.EmailVerificationCodeService;
import com.mole.health.service.UserService;
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
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-10-15 15:40:59
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final EmailVerificationCodeService emailVerificationCodeService;
    private final MinioUtil minioUtil;
    private final MinioProperties minioProperties;

    @Autowired
    public UserServiceImpl(EmailVerificationCodeService emailVerificationCodeService, MinioUtil minioUtil, MinioProperties minioProperties) {
        this.emailVerificationCodeService = emailVerificationCodeService;
        this.minioUtil = minioUtil;
        this.minioProperties = minioProperties;
    }

    @Override
    public String register(UserRegisterDto userRegisterDto) {
        validateRegistrationData(userRegisterDto);

        // 验证验证码
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail(userRegisterDto.getEmail());
        emailDto.setEmailVerificationCode(userRegisterDto.getEmailVerificationCode());
        if (!verifyCode(emailDto)) {
            Asserts.failBusiness("验证码错误或已过期");
        }

        User user = BeanUtil.copyProperties(userRegisterDto, User.class);
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        if (!this.save(user)) {
            Asserts.failBusiness("注册失败，请稍后重试");
        }

        // 注册成功后直接登录
        StpKit.USER.login(user.getId());
        return StpKit.USER.getTokenValue();
    }

    /**
     * 用户登录
     *
     * @param userLoginDto 包含登录信息的数据传输对象
     * @return 登录成功后的令牌
     * @throws com.mole.health.exception.BusinessException 当登录失败时抛出业务异常
     */
    @Override
    public String login(UserLoginDto userLoginDto) {
        // 首先检查用户是否已经登录
        if (checkLoginStatus()) {
            Asserts.failBusiness("用户已登录，请勿重复登录");
        }

        // 验证登录数据并获取用户信息
        User user = validateLoginData(userLoginDto);

        // 根据登录类型进行验证
        switch (userLoginDto.getLoginType()) {
            case USERNAME, EMAIL -> {
                // 验证密码
                if (StrUtil.isBlank(userLoginDto.getPassword()) || !BCrypt.checkpw(userLoginDto.getPassword(), user.getPassword())) {
                    Asserts.failBusiness("密码错误");
                }
            }
            case EMAIL_VERIFICATION_CODE -> {
                // 验证邮箱验证码
                EmailDto emailDto = new EmailDto();
                emailDto.setEmail(userLoginDto.getEmail());
                emailDto.setEmailVerificationCode(userLoginDto.getEmailVerificationCode());
                if (!verifyCode(emailDto)) {
                    Asserts.failBusiness("验证码错误或已过期");
                }
            }
            default -> Asserts.failBusiness("不支持的登录类型");
        }

        try {
            // 执行登录操作
            StpKit.USER.login(user.getId(), "user");
            // 返回登录令牌
            return StpKit.USER.getTokenValue();
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
            StpKit.USER.logout(StpKit.USER.getLoginIdAsLong(), "user");
            return "登出成功";
        } catch (Exception e) {
            Asserts.failBusiness("登出失败，请稍后重试");
        }
        return null;
    }

    private void validateRegistrationData(UserRegisterDto userRegisterDto) {
        if (userRegisterDto == null) {
            Asserts.failBusiness("注册信息不能为空");
        }
        if (StrUtil.isBlank(userRegisterDto.getUsername())) {
            Asserts.failBusiness("用户名不能为空");
        }
        if (StrUtil.isBlank(userRegisterDto.getPassword())) {
            Asserts.failBusiness("密码不能为空");
        }
        if (StrUtil.isBlank(userRegisterDto.getEmail())) {
            Asserts.failBusiness("邮箱不能为空");
        }
        if (StrUtil.isBlank(userRegisterDto.getEmailVerificationCode())) {
            Asserts.failBusiness("验证码不能为空");
        }

        if (this.getOne(new QueryWrapper<User>().eq("username", userRegisterDto.getUsername())) != null) {
            Asserts.failBusiness("用户名已存在", ResultCode.VALIDATE_FAILED);
        }

        if (this.getOne(new QueryWrapper<User>().eq("email", userRegisterDto.getEmail())) != null) {
            Asserts.failBusiness("邮箱已被使用");
        }
    }

    /**
     * 验证登录数据
     *
     * @param userLoginDto 包含登录信息的数据传输对象
     * @return 验证成功后的用户对象
     * @throws com.mole.health.exception.BusinessException 当验证失败时抛出业异常
     */
    private User validateLoginData(UserLoginDto userLoginDto) {
        // 检查登录信息是否为空
        if (ObjectUtil.isNull(userLoginDto)) {
            Asserts.failBusiness("登录信息不能为空");
        }

        User user = null;
        switch (userLoginDto.getLoginType()) {
            case USERNAME -> {
                if (StrUtil.isBlank(userLoginDto.getUsername())) {
                    Asserts.failBusiness("用户名不能为空");
                }
                user = this.getOne(new QueryWrapper<User>().eq("username", userLoginDto.getUsername()));
                if (user == null) {
                    Asserts.failBusiness("用户名不存在", ResultCode.UNAUTHORIZED);
                }
            }
            case EMAIL, EMAIL_VERIFICATION_CODE -> {
                if (StrUtil.isBlank(userLoginDto.getEmail())) {
                    Asserts.failBusiness("邮箱不能为空");
                }
                user = this.getOne(new QueryWrapper<User>().eq("email", userLoginDto.getEmail()));
                if (user == null) {
                    Asserts.failBusiness("邮箱不存在", ResultCode.UNAUTHORIZED);
                }
            }
            default -> Asserts.failBusiness("不支持的登录类型");
        }

        return user;
    }

    /**
     * 检查用户登录状态
     *
     * @return 用户是否已登录
     */
    @Override
    public boolean checkLoginStatus() {
        // 检查特定类型的登录状态
        return StpKit.USER.isLogin("user");
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
    public UserDto getUserInfo(String username, String email) {
        if (StrUtil.isBlank(username) && StrUtil.isBlank(email)) {
            Asserts.failBusiness("用户名和邮箱不能同时为空");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(username)) {
            queryWrapper.eq("username", username);
        } else {
            queryWrapper.eq("email", email);
        }

        User user = this.getOne(queryWrapper);
        if (user == null) {
            Asserts.failBusiness("用户不存在");
        }

        UserDto userDto = convertToUserDto(user);
        if (userDto.getAvatar() != null) {
            userDto.setAvatar(getFullAvatarUrl(userDto.getAvatar()));
        }
        return userDto;
    }

    /**
     * 将User对象转换为UserDto对象
     *
     * @param user 用户对象
     * @return UserDto对象
     */
    private UserDto convertToUserDto(User user) {
        UserDto userDto = new UserDto();
        BeanUtil.copyProperties(user, userDto, "password");
        return userDto;
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
        User user = this.getOne(new QueryWrapper<User>().eq("email", resetPasswordDto.getEmail()));
        if (user == null) {
            Asserts.failBusiness("用户不存在");
        }

        // 更新密码
        user.setPassword(BCrypt.hashpw(resetPasswordDto.getNewPassword()));
        if (!this.updateById(user)) {
            Asserts.failBusiness("密码重置失败，请稍后重试");
        }

        return "密码重置成功";
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateDto 用户更新数据传输对象
     * @return 更新后的用户信息
     */
    @Override
    public UserDto updateUserProfile(UserUpdateDto userUpdateDto) {
        Long userId = null;
        try {
            userId = StpKit.USER.getLoginIdAsLong();
            System.out.println("成功获取到用户ID: " + userId);
        } catch (Exception e) {
            System.out.println("获取用户ID失败: " + e.getMessage());
        }
        User user = this.getById(userId);
        if (user == null) {
            Asserts.failBusiness("用户不存在", ResultCode.VALIDATE_FAILED);
        }

        // 更新邮箱
        if (StrUtil.isNotBlank(userUpdateDto.getEmail()) && !userUpdateDto.getEmail().equals(user.getEmail())) {
            // 检查新邮箱是否已使用
            if (this.getOne(new QueryWrapper<User>().eq("email", userUpdateDto.getEmail())) != null) {
                Asserts.failBusiness("该邮箱已被使用", ResultCode.VALIDATE_FAILED);
            }

            // 验证邮箱验证码
            if (StrUtil.isBlank(userUpdateDto.getEmailVerificationCode())) {
                Asserts.failBusiness("更新邮箱需要验证码", ResultCode.VALIDATE_FAILED);
            }
            EmailDto emailDto = new EmailDto();
            emailDto.setEmail(userUpdateDto.getEmail());
            emailDto.setEmailVerificationCode(userUpdateDto.getEmailVerificationCode());
            System.out.println("emailDto: " + emailDto);
            if (!verifyCode(emailDto)) {
                Asserts.failBusiness("邮箱验证码错误或已过期", ResultCode.VALIDATE_FAILED);
            }

            user.setEmail(userUpdateDto.getEmail());
        }

        // 更新密码
        if (StrUtil.isNotBlank(userUpdateDto.getOldPassword()) && StrUtil.isNotBlank(userUpdateDto.getNewPassword())) {
            if (!BCrypt.checkpw(userUpdateDto.getOldPassword(), user.getPassword())) {
                Asserts.failBusiness("旧密码不正确", ResultCode.VALIDATE_FAILED);
            }
            user.setPassword(BCrypt.hashpw(userUpdateDto.getNewPassword()));
        }

        if (!this.updateById(user)) {
            Asserts.failBusiness("更新用户信息失败");
        }

        return convertToUserDto(user);
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
            Long userId = StpKit.USER.getLoginIdAsLong();
            User user = this.getById(userId);
            if (user == null) {
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
            String fileName = "avatar_user_" + userId + "_" + System.currentTimeMillis() + extension;

            // 上传文件到Minio
            minioUtil.uploadFileWithFileName(bucketName, fileName, file);

            // 只存储相对路径
            String avatarPath = bucketName + "/" + fileName;
            user.setAvatar(avatarPath);
            boolean updateSuccess = this.updateById(user);
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
     * 获取用户列表，支持用户名模糊搜索
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param username 用户名（可选，用于模糊搜索）
     * @return 用户列表
     */
    @Override
    public CommonPage<User> getUserList(int pageNum, int pageSize, String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        Page<User> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        return CommonPage.restPage(page);
    }

    /**
     * 批量添加或更新用户
     *
     * @param userSaveDtoList 用户存储数据传输对象列表
     * @return 是否操作成功
     */
    @Override
    @Transactional
    public boolean batchSaveOrUpdateUsers(List<UserSaveDto> userSaveDtoList) {
        List<User> userList = userSaveDtoList.stream().map(dto -> {
            User user;
            if (dto.getId() != null) {
                log.info("更新现有用户，ID: {}", dto.getId());
                // 更新现有用户
                user = this.getById(dto.getId());
                if (user == null) {
                    log.error("用户不存在，ID: {}", dto.getId());
                    Asserts.failBusiness("用户不存在，ID: " + dto.getId());
                }
                user.setUsername(dto.getUsername());
                user.setEmail(dto.getEmail());
                if (StrUtil.isNotBlank(dto.getPassword())) {
                    // 只有在提供了新密码时才更新密码
                    user.setPassword(BCrypt.hashpw(dto.getPassword()));
                }
            } else {
                log.info("新增用户，用户名: {}", dto.getUsername());
                // 新增用户
                user = new User();
                user.setUsername(dto.getUsername());
                user.setEmail(dto.getEmail());
                if (StrUtil.isBlank(dto.getPassword())) {
                    throw new RuntimeException("新用户密码不能为空");
                }
                user.setPassword(BCrypt.hashpw(dto.getPassword()));
            }
            return user;
        }).collect(Collectors.toList());

        return this.saveOrUpdateBatch(userList);
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 是否删除成功
     */
    @Override
    @Transactional
    public boolean batchDeleteUsers(List<Integer> ids) {
        return this.removeByIds(ids);
    }
}
