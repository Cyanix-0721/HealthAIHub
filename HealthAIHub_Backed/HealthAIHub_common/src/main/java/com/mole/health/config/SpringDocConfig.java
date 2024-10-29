package com.mole.health.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.http.HttpHeaders;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringDocConfig implements WebMvcConfigurer {

    /**
     * 配置OpenAPI的基本信息和安全设置
     *
     * @return OpenAPI实例
     */
    @Bean
    public OpenAPI HealthAIHubOpenAPI() {
        return new OpenAPI().info(new Info().title("健康伙伴 -- 个人健康助手").description("基于医疗大模型的个人健康助手").version("v1.0.0").license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html"))).addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION)).components(new Components().addSecuritySchemes(HttpHeaders.AUTHORIZATION, new SecurityScheme().name(HttpHeaders.AUTHORIZATION).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }

    /**
     * 配置全局OpenApi自定义器，解决Knife4j配置认证后无法自动添加认证头的问题
     *
     * @return GlobalOpenApiCustomizer实例
     */
    @Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            // 全局添加鉴权参数
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((s, pathItem) -> {
                    // 为所有接口添加鉴权
                    pathItem.readOperations().forEach(operation -> {
                        operation.addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
                    });
                });
            }
        };
    }

}

