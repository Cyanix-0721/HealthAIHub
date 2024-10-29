package com.mole.health.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员表
 *
 * @TableName admin
 */
@TableName(value = "admin")
@Data
@Schema(name = "Admin", description = "管理员表")
public class Admin implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 管理员ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "管理员ID")
    private Long id;
    /**
     * 管理员用户名
     */
    @TableField(value = "username")
    @Schema(description = "管理员用户名")
    private String username;
    /**
     * 管理员密码
     */
    @TableField(value = "password")
    @Schema(description = "管理员密码")
    private String password;
    /**
     * 头像
     */
    @TableField(value = "avatar")
    @Schema(description = "头像")
    private String avatar;
    /**
     * 管理员邮箱
     */
    @TableField(value = "email")
    @Schema(description = "管理员邮箱")
    private String email;
    /**
     * 注册时间
     */
    @TableField(value = "created_at")
    @Schema(description = "注册时间")
    private LocalDateTime createdAt;
}
