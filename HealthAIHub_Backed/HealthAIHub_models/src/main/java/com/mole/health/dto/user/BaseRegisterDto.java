package com.mole.health.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "基础注册数据传输对象")
public class BaseRegisterDto {

    @Schema(description = "用户名")
    protected String username;

    @Schema(description = "邮箱")
    protected String email;

    @Schema(description = "密码")
    protected String password;

    @Schema(description = "邮箱验证码")
    protected String emailVerificationCode;
}
