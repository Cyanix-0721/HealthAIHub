package com.mole.health;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.mole.health.mapper")
@EnableTransactionManagement
@SpringBootApplication
public class HealthAIHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthAIHubApplication.class, args);
    }
}
