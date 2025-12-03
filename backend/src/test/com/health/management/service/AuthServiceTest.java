package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.management.common.Result;
import com.health.management.dto.LoginRequest;
import com.health.management.dto.RegisterRequest;
import com.health.management.entity.StudentUser;
import com.health.management.mapper.AdminEmployeeListMapper;
import com.health.management.mapper.AdminUserMapper;
import com.health.management.mapper.StudentUserMapper;
import com.health.management.service.impl.AuthServiceImpl;
import com.health.management.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 认证服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("认证服务测试")
public class AuthServiceTest {

    @Mock
    private StudentUserMapper studentUserMapper;

    @Mock
    private AdminUserMapper adminUserMapper;

    @Mock
    private AdminEmployeeListMapper adminEmployeeListMapper;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("学生注册-成功")
    void testRegisterStudent_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setRole("STUDENT");
        request.setStudentNo("2021001");
        request.setPassword("password123");
        request.setName("张三");
        request.setGender(1);
        request.setPhone("13800138000");
        request.setEmail("zhangsan@example.com");

        when(studentUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(studentUserMapper.insert(any(StudentUser.class))).thenReturn(1);

        Result result = authService.register(request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(studentUserMapper, times(1)).insert(any(StudentUser.class));
    }

    @Test
    @DisplayName("学生注册-学号已存在")
    void testRegisterStudent_StudentNoExists() {
        RegisterRequest request = new RegisterRequest();
        request.setRole("STUDENT");
        request.setStudentNo("2021001");
        request.setPassword("password123");

        StudentUser existUser = new StudentUser();
        existUser.setId(1L);
        existUser.setStudentNo("2021001");

        when(studentUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(existUser);

        Result result = authService.register(request);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        verify(studentUserMapper, never()).insert(any(StudentUser.class));
    }

    @Test
    @DisplayName("学生登录-成功")
    void testLoginStudent_Success() {
        LoginRequest request = new LoginRequest();
        request.setRole("STUDENT");
        request.setStudentNo("2021001");
        request.setPassword("password123");

        StudentUser user = new StudentUser();
        user.setId(1L);
        user.setStudentNo("2021001");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setName("张三");
        user.setStatus(1);

        when(studentUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(user);
        when(jwtUtil.generateToken(anyLong(), anyString(), anyString())).thenReturn("mock-jwt-token");

        Result result = authService.login(request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(studentUserMapper, times(1)).selectOne(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("学生登录-用户不存在")
    void testLoginStudent_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setRole("STUDENT");
        request.setStudentNo("9999999");
        request.setPassword("password123");

        when(studentUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        Result result = authService.login(request);

        assertNotNull(result);
        assertEquals(500, result.getCode());
    }

    @Test
    @DisplayName("获取当前用户信息-成功")
    void testGetCurrentUser_Success() {
        String token = "valid-jwt-token";
        Long userId = 1L;
        String role = "STUDENT";

        StudentUser user = new StudentUser();
        user.setId(1L);
        user.setStudentNo("2021001");
        user.setName("张三");

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(userId);
        when(jwtUtil.getRoleFromToken(token)).thenReturn(role);
        when(studentUserMapper.selectById(1L)).thenReturn(user);

        Result result = authService.getCurrentUser(token);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(studentUserMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("获取当前用户信息-Token无效")
    void testGetCurrentUser_InvalidToken() {
        String token = "invalid-jwt-token";

        // 核心Mock：模拟Token校验失败
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // 执行测试
        Result result = authService.getCurrentUser(token);

        // 断言验证
        assertNotNull(result);
        assertEquals(401, result.getCode()); // 确保返回401
        verify(studentUserMapper, never()).selectById(any()); // 确保未查询数据库
    }
}
