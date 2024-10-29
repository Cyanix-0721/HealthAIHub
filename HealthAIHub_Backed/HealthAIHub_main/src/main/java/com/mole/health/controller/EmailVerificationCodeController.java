package com.mole.health.controller;

import com.mole.health.dto.user.EmailDto;
import com.mole.health.result.CommonResult;
import com.mole.health.service.EmailVerificationCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/email-verification")
@Tag(name = "EmailVerificationCodeController", description = "邮箱验证码控制器")
public class EmailVerificationCodeController {

    private final EmailVerificationCodeService emailVerificationCodeService;

    @Autowired
    public EmailVerificationCodeController(EmailVerificationCodeService emailVerificationCodeService) {
        this.emailVerificationCodeService = emailVerificationCodeService;
    }

    /**
     * 发送验证码接口
     *
     * @param emailDto 用户邮箱DTO
     * @return 发送结果
     */
    @PostMapping("/send-code")
    @Operation(summary = "发送验证码", description = "向指定邮箱发送验证码")
    @ApiResponse(responseCode = "200", description = "验证码发送成功")
    public CommonResult<String> sendVerificationCode(@RequestBody @Parameter(description = "用户邮箱") EmailDto emailDto) {
        try {
            String code = emailVerificationCodeService.generateCode(emailDto);
            emailDto.setEmailVerificationCode(code);
            emailVerificationCodeService.sendCode(emailDto);
            return CommonResult.success("验证码已发送");
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return CommonResult.failed("发送验证码失败：" + e.getMessage());
        }
    }

    /**
     * 验证验证码接口
     *
     * @param emailDto 包含邮箱和验证码的DTO
     * @return 验证结果
     */
    @PostMapping("/verify-code")
    @Operation(summary = "验证验证码", description = "验证用户输入的验证码是否正确")
    @ApiResponse(responseCode = "200", description = "验证码验证结果")
    public CommonResult<Boolean> verifyCode(@RequestBody @Parameter(description = "邮箱和验证码") EmailDto emailDto) {
        boolean isValid = emailVerificationCodeService.verifyCode(emailDto);
        return CommonResult.success(isValid);
    }
}
