package com.health.management.service;

import com.health.management.common.Result;
import com.health.management.dto.LoginRequest;
import com.health.management.dto.RegisterRequest;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户注册
     */
    Result register(RegisterRequest request);
    
    /**
     * 用户登录
     */
    Result login(LoginRequest request);

    /**
     * 用户登出
     */
    Result logout(String token);

    /**
     * 获取当前用户信息
     */
    Result getCurrentUser(String token);
}
