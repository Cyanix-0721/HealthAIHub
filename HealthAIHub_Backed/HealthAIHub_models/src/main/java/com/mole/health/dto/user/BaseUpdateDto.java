package com.mole.health.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "基础信息更新数据传输对象")
public class BaseUpdateDto {

    @Schema(description = "用户名")
    protected String username;

    @Schema(description = "邮箱")
    protected String email;

    @Schema(description = "邮箱验证码")
    protected String emailVerificationCode;

    @Schema(description = "旧密码")
    protected String oldPassword;

    @Schema(description = "新密码")
    protected String newPassword;
}
