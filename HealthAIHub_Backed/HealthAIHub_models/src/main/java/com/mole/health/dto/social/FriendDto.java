package com.mole.health.dto.social;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FriendDto", description = "好友信息Dto")
public class FriendDto {
    @Schema(description = "好友ID")
    private Long id;

    @Schema(description = "好友用户名")
    private String username;

    @Schema(description = "好友头像")
    private String avatar;

    @Schema(description = "好友状态")
    private Integer status;
}
