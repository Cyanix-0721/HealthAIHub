package com.mole.health.controller;

import com.mole.health.result.CommonResult;
import com.mole.health.util.EmailUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@Tag(name = "EmailController", description = "邮件发送接口")
public class EmailController {

    private final EmailUtil emailUtil;

    @Autowired
    public EmailController(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    /**
     * 发送简单文本邮件
     *
     * @param to      收件人邮箱地址
     * @param subject 邮件主题
     * @param text    邮件内容
     * @return CommonResult<String> 返回结果
     */
    @PostMapping("/simple")
    @Operation(summary = "发送简单文本邮件", description = "发送简单文本邮件")
    public CommonResult<String> sendSimpleEmail(@RequestParam String to,
                                                @RequestParam String subject,
                                                @RequestParam String text) {
        try {
            emailUtil.sendSimpleEmail(to, subject, text);
            return CommonResult.success("Simple email sent successfully");
        } catch (Exception e) {
            return CommonResult.failed("Error sending simple email: " + e.getMessage());
        }
    }
}
