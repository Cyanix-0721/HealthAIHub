package com.mole.health.service.impl;

import com.mole.health.dto.user.EmailDto;
import com.mole.health.service.EmailVerificationCodeService;
import com.mole.health.util.EmailUtil;
import com.mole.health.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class EmailVerificationCodeServiceImpl implements EmailVerificationCodeService {

    private static final long CODE_EXPIRE_TIME = 30; // 30分钟
    private final RedisUtil redisUtil;
    private final EmailUtil emailUtil;

    @Autowired
    public EmailVerificationCodeServiceImpl(RedisUtil redisUtil, EmailUtil emailUtil) {
        this.redisUtil = redisUtil;
        this.emailUtil = emailUtil;
    }

    @Override
    public String generateCode(EmailDto emailDto) {
        String code = generateRandomCode();
        redisUtil.set(getRedisKey(emailDto.getEmail()), code, CODE_EXPIRE_TIME * 60); // 转换为秒
        log.info("生成验证码：{}", code);
        return code;
    }

    @Override
    public void sendCode(EmailDto emailDto) {
        String code = emailDto.getEmailVerificationCode();
        emailUtil.sendSimpleEmail(emailDto.getEmail(), "验证码", "您的验证码是：" + code);
        log.info("发送验证码：{}", code);
    }

    @Override
    public boolean verifyCode(EmailDto emailDto) {
        String storedCode = (String) redisUtil.get(getRedisKey(emailDto.getEmail()));
        log.info("验证验证码：{}", storedCode);
        return emailDto.getEmailVerificationCode().equals(storedCode);
    }

    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String getRedisKey(String email) {
        return "email_verification_code:" + email;
    }
}
