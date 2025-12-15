package com.health.management.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


/**
 * 测试配置类
 * 提供测试所需的Bean配置
 */
@TestConfiguration
public class TestConfig {
    
    /**
     * 密码编码器Bean
     */
    @Bean
    public PasswordEncoderConfig.PasswordEncoder passwordEncoder() {
        return new PasswordEncoderConfig.PasswordEncoder();
    }
}
