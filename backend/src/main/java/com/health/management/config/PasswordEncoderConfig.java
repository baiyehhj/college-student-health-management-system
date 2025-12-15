package com.health.management.config;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 密码编码器配置（不依赖Spring Security）
 * 基于独立BCrypt库实现密码加密/验证
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 自定义密码编码器Bean（兼容原PasswordEncoder接口逻辑）
     * 对外暴露加密/验证方法，保持调用方式不变
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder();
    }

    /**
     * 自定义密码编码器（替代Spring Security的PasswordEncoder）
     * 基于jbcrypt实现BCrypt加密逻辑
     */
    public static class PasswordEncoder {
        // BCrypt加密强度（4-31，值越大越安全但耗时越长，推荐10-12）
        private static final int BCRYPT_STRENGTH = 10;

        /**
         * 加密明文密码（对应原encode方法）
         */
        public String encode(CharSequence rawPassword) {
            if (rawPassword == null) {
                throw new IllegalArgumentException("密码不能为空");
            }
            // 生成盐并加密
            String salt = BCrypt.gensalt(BCRYPT_STRENGTH);
            return BCrypt.hashpw(rawPassword.toString(), salt);
        }

        /**
         * 验证明文密码是否匹配加密后的密码（对应原matches方法）
         */
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            if (rawPassword == null || encodedPassword == null) {
                return false;
            }
            try {
                return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
            } catch (IllegalArgumentException e) {
                // 加密字符串格式错误时返回不匹配
                return false;
            }
        }
    }
}