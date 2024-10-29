package com.mole.health.websocket;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mole.health.pojo.UserAiInteraction;
import com.mole.health.repository.UserAiInteractionRepository;
import com.mole.health.util.RedisUtil;
import com.mole.health.util.TokenUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 处理 AI 聊天 WebSocket 连接的处理器。
 */
@Slf4j
@Component
public class AiChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ZhiPuAiChatModel chatModel;
    private final RedisUtil redisUtil;
    private final UserAiInteractionRepository userAiInteractionRepository;
    private final TokenUtil tokenUtil;
    private final String basePrompt;

    /**
     * 构造函数，注入所需的依赖。
     *
     * @param objectMapper                用于 JSON 序列化和反序列化的 ObjectMapper
     * @param chatModel                   用于处理聊天的 AI 模型
     * @param redisUtil                   用于 Redis 操作的工具类
     * @param userAiInteractionRepository 用户 AI 交互的存储库
     */
    @Autowired
    public AiChatWebSocketHandler(ObjectMapper objectMapper, ZhiPuAiChatModel chatModel, RedisUtil redisUtil, UserAiInteractionRepository userAiInteractionRepository, TokenUtil tokenUtil) {
        this.objectMapper = objectMapper;
        this.chatModel = chatModel;
        this.redisUtil = redisUtil;
        this.userAiInteractionRepository = userAiInteractionRepository;
        this.tokenUtil = tokenUtil;
        this.basePrompt = "你是一个专业的健康助手AI。请根据以下问题提供简洁、准确的健康建议。如果遇到需要专业医疗诊断的问题，请建议用户咨询医生：\n\n";
    }

    /**
     * 处理收到的文本消息。
     *
     * @param session 当前的 WebSocket 会话
     * @param message 收到的文本消息
     * @throws Exception 处理消息时可能抛出的异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Received message: {}", message.getPayload());
        String userId = getUserIdFromSession(session);
        String sessionId = getSessionIdFromSession(session);
        String question = message.getPayload();
        log.info("User ID: {}, Session ID: {}, Question: {}", userId, sessionId, question);

        String cacheKey = "user_ai_interaction:" + userId + ":" + sessionId;
        UserAiInteraction interaction;

        // 尝试从 Redis 缓存中获取交互信息
        String cachedInteraction = (String) redisUtil.get(cacheKey);
        log.info("Cached interaction: {}", cachedInteraction);
        if (cachedInteraction != null) {
            interaction = objectMapper.readValue(cachedInteraction, UserAiInteraction.class);
        } else {
            // 如果缓存中没有，则从数据库中获取
            Optional<UserAiInteraction> interactionOpt = userAiInteractionRepository.findByUserIdAndSessionId(userId, sessionId);
            interaction = interactionOpt.orElseGet(() -> {
                return UserAiInteraction.builder().userId(userId).sessionId(sessionId).context(new ArrayList<>()).timestamp(Instant.now()).build();
            });
        }

        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage(basePrompt));
        for (UserAiInteraction.Message contextMessage : interaction.getContext()) {
            messages.add(new UserMessage(contextMessage.getContent()));
        }
        messages.add(new UserMessage(question));

        var prompt = new Prompt(messages);
        ChatResponse response = chatModel.call(prompt);
        String answer = response.getResult().getOutput().getContent();
        log.info("Answer: {}", answer);

        interaction.getContext().add(UserAiInteraction.Message.builder().role("user").content(question).build());

        interaction.getContext().add(UserAiInteraction.Message.builder().role("assistant").content(answer).build());
        userAiInteractionRepository.save(interaction);

        session.sendMessage(new TextMessage(answer));
        log.info("Sent answer: {}", answer);
    }

    /**
     * 从 WebSocket 会话中获取用户 ID。
     *
     * @param session 当前的 WebSocket 会话
     * @return 用户 ID
     */
    private String getUserIdFromSession(WebSocketSession session) {
        return session.getAttributes().get("userId").toString();
    }

    /**
     * 从 WebSocket 会话中获取会话 ID。
     *
     * @param session 当前的 WebSocket 会话
     * @return 会话 ID
     */
    private String getSessionIdFromSession(WebSocketSession session) {
        // 直接返回 WebSocket 会话的 ID
        return session.getId();
    }

    /**
     * 在连接建立时进行初始化操作。
     *
     * @param session 当前的 WebSocket 会话
     * @throws Exception 初始化操作时可能抛出的异常
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = Objects.requireNonNull(session.getUri()).getQuery();
        String token = null;
        if (query != null && query.startsWith("token=")) {
            token = query.substring(6);
        }

        if (token != null && !token.isEmpty()) {
            // 验证 token
            String userId = tokenUtil.validateTokenAndGetUserId(token);
            if (userId != null) {
                session.getAttributes().put("userId", userId);
                log.info("WebSocket connection established for user: {}", userId);
            } else {
                // token 无效，关闭连接
                session.close(CloseStatus.POLICY_VIOLATION);
                log.info("WebSocket connection closed due to invalid token");
            }
        } else {
            // 没有提供 token，关闭连接
            session.close(CloseStatus.POLICY_VIOLATION);
            log.info("WebSocket connection closed due to missing token");
        }
    }
}
