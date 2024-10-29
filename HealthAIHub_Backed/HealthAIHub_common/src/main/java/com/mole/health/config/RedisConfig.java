package com.mole.health.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis基础配置
 * Created by macro on 2020/6/19.
 * Modified by Cyanix-0721 on 2024/9/28.
 */
@Configuration
public class RedisConfig {

    /**
     * 配置 RedisTemplate
     *
     * @param redisConnectionFactory Redis 连接工厂
     * @return RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 创建一个 RedisSerializer 对象，用于序列化 Redis 中的值
        RedisSerializer<Object> serializer = redisSerializer();

        // 创建一个 RedisTemplate 对象，用于执行 Redis 操作
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 设置 Redis 连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置键的序列化器为 StringRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 设置值的序列化器为自定义的 JSON 序列化器
        redisTemplate.setValueSerializer(serializer);

        // 设置哈希键的序列化器为 StringRedisSerializer
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 设置哈希值的序列化器为自定义的 JSON 序列化器
        redisTemplate.setHashValueSerializer(serializer);

        // 在设置完所有属性后，调用 afterPropertiesSet 方法，确保所有属性都已设置
        redisTemplate.afterPropertiesSet();

        // 返回配置好的 RedisTemplate 实例
        return redisTemplate;
    }

    /**
     * 配置 Redis 序列化器
     *
     * @return RedisSerializer 实例
     */
    @Bean
    public RedisSerializer<Object> redisSerializer() {

        // 创建 ObjectMapper 对象，用于配置 JSON 序列化
        ObjectMapper objectMapper = new ObjectMapper();

        // 设置所有访问权限的可见性
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 启用默认类型，必须设置，否则无法将 JSON 转化为对象，会转化成 Map 类型
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        // 创建 JSON 序列化器, 将配置好的 ObjectMapper 设置到 JSON 序列化器中
        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }

    /**
     * 配置 Redis 缓存管理器
     *
     * @param redisConnectionFactory Redis 连接工厂
     * @return RedisCacheManager 实例
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 创建一个 RedisCacheWriter 对象，不会锁定缓存
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);

        // 配置 Redis 缓存，设置默认过期时间为 1 天
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer())).entryTtl(Duration.ofDays(1));

        // 返回配置好的 RedisCacheManager 实例
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }
}