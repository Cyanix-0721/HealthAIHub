package com.mole.health.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class GlobalCorsConfig {

    /**
     * 创建一个CORS过滤器Bean，允许所有来源、头和方法。
     *
     * @return 配置了指定CORS设置的CorsWebFilter。
     */
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*"); // 允许所有HTTP方法
        config.addAllowedOriginPattern("*"); // 允许所有来源
        config.addAllowedHeader("*"); // 允许所有头
        config.setAllowCredentials(true); // 允许凭证（如Cookie、授权头等）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config); // 将CORS配置应用于所有路径

        return new CorsWebFilter(source);
    }

}