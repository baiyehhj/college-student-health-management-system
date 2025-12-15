package com.health.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.management.common.Result;
import com.health.management.dto.LoginRequest;
import com.health.management.dto.RegisterRequest;
import com.health.management.mapper.AIHealthReportMapper;
import com.health.management.mapper.StudentUserMapper;
import com.health.management.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 改用全上下文测试，确保AuthController被注册
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 模拟Service和Mapper（避免依赖真实数据库）
    @MockBean
    private AuthService authService;

    @MockBean
    private StudentUserMapper studentUserMapper;

    @MockBean
    private AIHealthReportMapper aiHealthReportMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // 初始化注册请求
        registerRequest = new RegisterRequest();
        registerRequest.setRole("student"); // 新增：设置角色
        registerRequest.setStudentNo("2021001");
        registerRequest.setPassword("123456");
        registerRequest.setName("测试学生");
        registerRequest.setGender(1);
        registerRequest.setPhone("13800138000");
        registerRequest.setEmail("test@example.com");
        registerRequest.setMajor("计算机科学");
        registerRequest.setClassName("计算机2021-1班");

        // 初始化登录请求
        loginRequest = new LoginRequest();
        loginRequest.setRole("student"); // 新增：设置角色
        loginRequest.setStudentNo("2021001");
        loginRequest.setEmployeeNo(null);
        loginRequest.setPassword("123456");
    }

    @Test
    @DisplayName("注册接口")
    void testRegister_Success() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(Result.success("注册成功"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }


    @Test
    @DisplayName("登录接口-成功场景")
    void testLogin_Success() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(Result.success("登录成功", "test.jwt.token"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data").value("test.jwt.token"));
    }

    @Test
    @DisplayName("登录接口-用户不存在")
    void testLogin_UserNotExists() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(Result.error("用户不存在"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }


    @Test
    @DisplayName("获取当前用户-成功场景")
    void testGetCurrentUser_Success() throws Exception {
        when(authService.getCurrentUser(anyString()))
                .thenReturn(Result.success("查询成功", "测试用户"));

        mockMvc.perform(get("/auth/current")
                        .header("Authorization", "Bearer test.jwt.token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("测试用户"));
    }

    // @Test
    // @DisplayName("获取当前用户-缺少Token")
    // void testGetCurrentUser_MissingToken() throws Exception {
    //     mockMvc.perform(get("/auth/current")
    //                     .accept(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.code").value(400))
    //             .andExpect(jsonPath("$.message").value("缺少必要的请求头：Authorization"))
    //             .andExpect(jsonPath("$.data").isEmpty());
    // }

    @Test
    @DisplayName("获取当前用户-缺少Token")
    void testGetCurrentUser_MissingToken() throws Exception {
        mockMvc.perform(get("/auth/current")
                        // 仅声明接受JSON
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                // 1. 验证HTTP状态码为400
                .andExpect(status().isBadRequest())
                // 2. 验证异常类型
                .andExpect(result -> {
                    // 验证异常是MissingRequestHeaderException
                    assert result.getResolvedException() instanceof org.springframework.web.bind.MissingRequestHeaderException;
                    // 验证异常消息包含Authorization
                    assert result.getResolvedException().getMessage().contains("Authorization");
                })
                // 3. 验证Spring默认错误响应的特征
                .andExpect(jsonPath("$").doesNotExist()); // 响应体为空，跳过JSON断言
    }
}

