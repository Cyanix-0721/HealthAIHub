package com.mole.health.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "基础信息存储数据传输对象")
public class BaseSaveDto {
    @Schema(description = "主键")
    protected Integer id;

    @Schema(description = "用户名")
    protected String username;

    @Schema(description = "密码")
    protected String password;

    @Schema(description = "邮箱")
    protected String email;
}
