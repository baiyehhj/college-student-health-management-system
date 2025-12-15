package com.health.management.controller;

import com.health.management.common.Result;
import com.health.management.dto.LoginRequest;
import com.health.management.dto.RegisterRequest;
import com.health.management.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 学生注册
     */
    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
    
    /**
     * 学生登录
     */
    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
    
    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result logout(@RequestHeader(value = "Authorization", required = false) String token) {
        // 可选：添加token校验逻辑
        if (token == null || token.trim().isEmpty()) {
            return Result.error("登出失败，缺少令牌");
        }
        return Result.success("登出成功");
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result getCurrentUser(@RequestHeader("Authorization") String token) {
        return authService.getCurrentUser(token);
    }
}
