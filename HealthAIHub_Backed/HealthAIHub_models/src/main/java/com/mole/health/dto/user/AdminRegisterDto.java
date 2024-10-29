package com.mole.health.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "管理员注册数据传输对象")
public class AdminRegisterDto extends BaseRegisterDto {
}
