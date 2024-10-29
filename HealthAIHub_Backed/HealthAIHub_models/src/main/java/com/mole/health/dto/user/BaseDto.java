package com.mole.health.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Schema(description = "基础数据传输对象")
public class BaseDto implements Serializable {
    @Schema(description = "ID")
    protected Long id;

    @Schema(description = "用户名")
    protected String username;

    @Schema(description = "邮箱")
    protected String email;
    
    @Schema(description = "头像")
    private String avatar;
}
