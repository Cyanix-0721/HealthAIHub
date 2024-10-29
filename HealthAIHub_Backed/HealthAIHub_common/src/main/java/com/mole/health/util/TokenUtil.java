package com.mole.health.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenUtil {

    public String validateTokenAndGetUserId(String token) {
        try {
            // 使用 Sa-Token 验证 token
            Object loginId = StpKit.USER.getLoginIdByToken(token);
            if (loginId != null) {
                return loginId.toString();
            }
        } catch (Exception e) {
            // 处理验证过程中的异常
            log.error("Token validation error", e);
        }
        return null;
    }
}
