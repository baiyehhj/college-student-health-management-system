package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.management.common.Result;
import com.health.management.config.PasswordEncoderConfig;
import com.health.management.dto.LoginRequest;
import com.health.management.dto.RegisterRequest;
import com.health.management.entity.AdminEmployeeList;
import com.health.management.entity.AdminUser;
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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 认证服务测试类
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

    @Mock
    private PasswordEncoderConfig.PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest studentRegisterRequest;
    private RegisterRequest adminRegisterRequest;
    private LoginRequest studentLoginRequest;
    private LoginRequest adminLoginRequest;
    private StudentUser testStudent;
    private AdminUser testAdmin;
    private AdminEmployeeList testEmployee;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 初始化密码
        String mockPassword = "encrypted_password";

        // 学生注册请求
        studentRegisterRequest = new RegisterRequest();
        studentRegisterRequest.setRole("STUDENT");
        studentRegisterRequest.setStudentNo("2021001");
        studentRegisterRequest.setPassword("123456");
        studentRegisterRequest.setName("测试学生");
        studentRegisterRequest.setGender(1);
        studentRegisterRequest.setPhone("13800138000");
        studentRegisterRequest.setEmail("test@example.com");
        studentRegisterRequest.setMajor("计算机科学");
        studentRegisterRequest.setClassName("计算机2021-1班");

        // 管理员注册请求
        adminRegisterRequest = new RegisterRequest();
        adminRegisterRequest.setRole("ADMIN");
        adminRegisterRequest.setEmployeeNo("E001");
        adminRegisterRequest.setPassword("admin123");
        adminRegisterRequest.setName("测试管理员");
        adminRegisterRequest.setGender(1);
        adminRegisterRequest.setPhone("13900139000");
        adminRegisterRequest.setEmail("admin@example.com");

        // 学生登录请求
        studentLoginRequest = new LoginRequest();
        studentLoginRequest.setRole("STUDENT");
        studentLoginRequest.setStudentNo("2021001");
        studentLoginRequest.setPassword("123456");

        // 管理员登录请求
        adminLoginRequest = new LoginRequest();
        adminLoginRequest.setRole("ADMIN");
        adminLoginRequest.setEmployeeNo("E001");
        adminLoginRequest.setPassword("admin123");

        // 测试学生数据
        testStudent = new StudentUser();
        testStudent.setId(1L);
        testStudent.setStudentNo("2021001");
        testStudent.setPassword(mockPassword);
        testStudent.setName("测试学生");
        testStudent.setGender(1);
        testStudent.setAge(20);
        testStudent.setStatus(1);

        // 测试管理员数据
        testAdmin = new AdminUser();
        testAdmin.setId(1L);
        testAdmin.setEmployeeNo("E001");
        testAdmin.setPassword(mockPassword);
        testAdmin.setName("测试管理员");
        testAdmin.setGender(1);
        testAdmin.setDepartment("信息技术部");
        testAdmin.setStatus(1);

        // 员工列表数据
        testEmployee = new AdminEmployeeList();
        testEmployee.setId(1L);
        testEmployee.setEmployeeNo("E001");
        testEmployee.setDepartment("信息技术部");
        testEmployee.setIsRegistered(0);
    }

    @Test
    @DisplayName("学生注册 - 成功")
    public void testRegisterStudent_Success() {
        // Given
        when(studentUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(studentUserMapper.insert(any(StudentUser.class))).thenReturn(1);

        // When
        Result result = authService.register(studentRegisterRequest);

        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("注册成功") || result.getMessage().equals("注册成功"));
        verify(studentUserMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(studentUserMapper, times(1)).insert(any(StudentUser.class));
    }

    @Test
    @DisplayName("学生注册 - 学号已存在")
    public void testRegisterStudent_StudentNoExists() {
        // Given
        when(studentUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(testStudent);

        // When
        Result result = authService.register(studentRegisterRequest);

        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("学号") && result.getMessage().contains("已存在")
                || result.getMessage().contains("学号") && result.getMessage().contains("已被注册"));
        verify(studentUserMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(studentUserMapper, never()).insert(any(StudentUser.class));
    }

    @Test
    @DisplayName("学生注册 - 学号为空")
    public void testRegisterStudent_EmptyStudentNo() {
        // Given
        studentRegisterRequest.setStudentNo("");

        // When
        Result result = authService.register(studentRegisterRequest);

        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("学号") && result.getMessage().contains("不能为空")
                || result.getMessage().equals("学号不能为空"));
        verify(studentUserMapper, never()).selectOne(any(QueryWrapper.class));
        verify(studentUserMapper, never()).insert(any(StudentUser.class));
    }

    @Test
    @DisplayName("管理员注册 - 成功")
    public void testRegisterAdmin_Success() {
        // Given
        when(adminEmployeeListMapper.selectOne(any(QueryWrapper.class))).thenReturn(testEmployee);
        when(adminUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(adminUserMapper.insert(any(AdminUser.class))).thenReturn(1);
        when(adminEmployeeListMapper.updateById(any(AdminEmployeeList.class))).thenReturn(1);

        // When
        Result result = authService.register(adminRegisterRequest);

        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("注册成功") || result.getMessage().equals("注册成功"));
        verify(adminEmployeeListMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(adminUserMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(adminUserMapper, times(1)).insert(any(AdminUser.class));
        verify(adminEmployeeListMapper, times(1)).updateById(any(AdminEmployeeList.class));
    }

    @Test
    @DisplayName("管理员注册 - 工号无效")
    public void testRegisterAdmin_InvalidEmployeeNo() {
        // Given
        when(adminEmployeeListMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // When
        Result result = authService.register(adminRegisterRequest);

        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("工号无效")
                || result.getMessage().contains("工号不存在")
                || result.getMessage().equals("该工号未备案，无法注册"));
        verify(adminEmployeeListMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(adminUserMapper, never()).selectOne(any(QueryWrapper.class));
        verify(adminUserMapper, never()).insert(any(AdminUser.class));
    }

    @Test
    @DisplayName("管理员注册 - 工号已注册")
    public void testRegisterAdmin_EmployeeNoAlreadyRegistered() {
        // Given
        // 模拟：工号已备案且已注册（isRegistered=1）
        testEmployee.setIsRegistered(1);
        when(adminEmployeeListMapper.selectOne(any(QueryWrapper.class))).thenReturn(testEmployee);

        // When
        Result result = authService.register(adminRegisterRequest);

        // Then
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("工号") && result.getMessage().contains("该工号已被注册"));
        verify(adminEmployeeListMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(adminUserMapper, never()).selectOne(any(QueryWrapper.class));
        verify(adminUserMapper, never()).insert(any(AdminUser.class));
    }

    @Test
    @DisplayName("学生登录 - 成功")
    public void testLoginStudent_Success() {
        // Given
        when(studentUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(testStudent);
        when(passwordEncoder.matches(eq("123456"), eq(testStudent.getPassword()))).thenReturn(true);
        when(jwtUtil.generateToken(anyLong(), anyString(), eq("STUDENT"))).thenReturn("test.jwt.token");

        // When
        Result result = authService.login(studentLoginRequest);

        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(studentUserMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(passwordEncoder, times(1)).matches(eq("123456"), eq(testStudent.getPassword()));
        verify(jwtUtil, times(1)).generateToken(anyLong(), anyString(), eq("STUDENT"));
    }

    @Test
    @DisplayName("学生登录 - 用户不存在")
    public void testLoginStudent_UserNotExists() {
        // Given
        // 模拟：查询不到学生数据（用户不存在）
        when(studentUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // When
        Result result = authService.login(studentLoginRequest);

        // Then
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().equals("用户不存在")
                || result.getMessage().equals("学号或密码错误")
                || result.getMessage().equals("工号或密码错误"));
        verify(studentUserMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("学生登录 - 密码错误")
    public void testLoginStudent_WrongPassword() {
        // Given
        studentLoginRequest.setPassword("wrongpassword");
        when(studentUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(testStudent);
        when(passwordEncoder.matches(eq("wrongpassword"), eq(testStudent.getPassword()))).thenReturn(false);

        // When
        Result result = authService.login(studentLoginRequest);

        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("密码错误")
                || result.getMessage().equals("学号不存在或密码错误")
                || result.getMessage().contains("密码不正确"));
        verify(studentUserMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(passwordEncoder, times(1)).matches(eq("wrongpassword"), eq(testStudent.getPassword()));
        verify(jwtUtil, never()).generateToken(anyLong(), anyString(), anyString());
    }


    @Test
    @DisplayName("学生登录 - 账号已禁用")
    public void testLoginStudent_AccountDisabled() {
        // Given
        testStudent.setStatus(0); // 0=禁用
        when(studentUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(testStudent);

        // When
        Result result = authService.login(studentLoginRequest);

        // Then
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().equals("账号已被禁用"));
        verify(studentUserMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("学生登录 - 学号为空")
    public void testLoginStudent_EmptyStudentNo() {
        // Given
        studentLoginRequest.setStudentNo("");

        // When
        Result result = authService.login(studentLoginRequest);

        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("学号不能为空")
                || result.getMessage().equals("请输入学号"));
        verify(studentUserMapper, never()).selectOne(any(QueryWrapper.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("管理员登录 - 成功")
    public void testLoginAdmin_Success() {
        // Given
        when(adminUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(testAdmin);
        when(passwordEncoder.matches(eq("admin123"), eq(testAdmin.getPassword()))).thenReturn(true);
        when(jwtUtil.generateToken(anyLong(), anyString(), eq("ADMIN"))).thenReturn("test.admin.token");

        // When
        Result result = authService.login(adminLoginRequest);

        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(adminUserMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(passwordEncoder, times(1)).matches(eq("admin123"), eq(testAdmin.getPassword()));
        verify(jwtUtil, times(1)).generateToken(anyLong(), anyString(), eq("ADMIN"));
    }

    @Test
    @DisplayName("管理员登录 - 工号为空")
    public void testLoginAdmin_EmptyEmployeeNo() {
        // Given
        adminLoginRequest.setEmployeeNo("");

        // When
        Result result = authService.login(adminLoginRequest);

        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("工号不能为空")
                || result.getMessage().equals("请输入工号"));
        verify(adminUserMapper, never()).selectOne(any(QueryWrapper.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("获取当前用户信息 - 学生用户成功")
    public void testGetCurrentUser_StudentSuccess() {
        // Given
        String token = "Bearer test.jwt.token";
        // 移除Bearer前缀（适配真实业务逻辑）
        String cleanToken = token.replace("Bearer ", "");
        when(jwtUtil.validateToken(cleanToken)).thenReturn(true);
        when(jwtUtil.getUserIdFromToken(cleanToken)).thenReturn(1L);
        when(jwtUtil.getRoleFromToken(cleanToken)).thenReturn("STUDENT");
        when(studentUserMapper.selectById(1L)).thenReturn(testStudent);

        // When
        Result result = authService.getCurrentUser(token);

        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(jwtUtil, times(1)).validateToken(cleanToken);
        verify(jwtUtil, times(1)).getUserIdFromToken(cleanToken);
        verify(jwtUtil, times(1)).getRoleFromToken(cleanToken);
        verify(studentUserMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("获取当前用户信息 - 管理员用户成功")
    public void testGetCurrentUser_AdminSuccess() {
        // Given
        String token = "Bearer test.admin.token";
        String cleanToken = token.replace("Bearer ", "");
        when(jwtUtil.validateToken(cleanToken)).thenReturn(true);
        when(jwtUtil.getUserIdFromToken(cleanToken)).thenReturn(1L);
        when(jwtUtil.getRoleFromToken(cleanToken)).thenReturn("ADMIN");
        when(adminUserMapper.selectById(1L)).thenReturn(testAdmin);

        // When
        Result result = authService.getCurrentUser(token);

        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(jwtUtil, times(1)).validateToken(cleanToken);
        verify(jwtUtil, times(1)).getUserIdFromToken(cleanToken);
        verify(jwtUtil, times(1)).getRoleFromToken(cleanToken);
        verify(adminUserMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("获取当前用户信息 - Token无效")
    public void testGetCurrentUser_InvalidToken() {
        // Given
        String token = "Bearer invalid.token";
        String cleanToken = token.replace("Bearer ", "");
        when(jwtUtil.validateToken(cleanToken)).thenReturn(false);

        // When
        Result result = authService.getCurrentUser(token);

        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("Token无效")
                || result.getMessage().contains("令牌无效")
                || result.getMessage().equals("登录已过期，请重新登录"));
        verify(jwtUtil, times(1)).validateToken(cleanToken);
        verify(jwtUtil, never()).getUserIdFromToken(anyString());
        verify(jwtUtil, never()).getRoleFromToken(anyString());
        verify(studentUserMapper, never()).selectById(anyLong());
        verify(adminUserMapper, never()).selectById(anyLong());
    }

    @Test
    @DisplayName("获取当前用户信息 - 用户不存在")
    public void testGetCurrentUser_UserNotExists() {
        // Given
        String token = "Bearer test.jwt.token";
        String cleanToken = token.replace("Bearer ", "");
        when(jwtUtil.validateToken(cleanToken)).thenReturn(true);
        when(jwtUtil.getUserIdFromToken(cleanToken)).thenReturn(999L);
        when(jwtUtil.getRoleFromToken(cleanToken)).thenReturn("STUDENT");
        when(studentUserMapper.selectById(999L)).thenReturn(null);

        // When
        Result result = authService.getCurrentUser(token);

        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("用户不存在")
                || result.getMessage().equals("该用户已注销"));
        verify(jwtUtil, times(1)).validateToken(cleanToken);
        verify(jwtUtil, times(1)).getUserIdFromToken(cleanToken);
        verify(jwtUtil, times(1)).getRoleFromToken(cleanToken);
        verify(studentUserMapper, times(1)).selectById(999L);
    }
}



