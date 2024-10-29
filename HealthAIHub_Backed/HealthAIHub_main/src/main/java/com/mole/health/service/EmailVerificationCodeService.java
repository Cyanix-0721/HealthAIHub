package com.mole.health.service;

import com.mole.health.dto.user.EmailDto;

public interface EmailVerificationCodeService {

    /**
     * 生成验证码
     *
     * @param emailDto 包含邮箱的DTO
     * @return 生成的验证码
     */
    String generateCode(EmailDto emailDto);

    /**
     * 发送验证码
     *
     * @param emailDto 包含邮箱和验证码的DTO
     */
    void sendCode(EmailDto emailDto);

    /**
     * 验证验证码
     *
     * @param emailDto 包含邮箱和验证码的DTO
     * @return 验证结果
     */
    boolean verifyCode(EmailDto emailDto);
}
