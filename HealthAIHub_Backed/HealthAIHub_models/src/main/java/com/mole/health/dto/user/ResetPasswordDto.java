package com.mole.health.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "邮箱数据传输对象")
public class ResetPasswordDto {

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "新密码")
    private String newPassword;

    @Schema(description = "邮箱验证码")
    private String emailVerificationCode;

}
