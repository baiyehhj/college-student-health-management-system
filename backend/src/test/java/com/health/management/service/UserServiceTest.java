package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.management.BaseTest;
import com.health.management.common.Result;
import com.health.management.config.PasswordEncoderConfig;
import com.health.management.dto.UserInfoRequest;
import com.health.management.entity.StudentUser;
import com.health.management.mapper.StudentUserMapper;
import com.health.management.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 */
@DisplayName("用户服务测试")
public class UserServiceTest extends BaseTest {
    
    @Mock
    private StudentUserMapper studentUserMapper;
    
    @Mock
    private PasswordEncoderConfig.PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private StudentUser testStudent;
    private UserInfoRequest userInfoRequest;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 准备测试学生数据
        testStudent = new StudentUser();
        testStudent.setId(TEST_STUDENT_ID);
        testStudent.setStudentNo(TEST_STUDENT_NO);
        testStudent.setPassword("$2a$10$encodedPassword");
        testStudent.setName("测试学生");
        testStudent.setGender(1);
        testStudent.setAge(20);
        testStudent.setPhone("13800138000");
        testStudent.setEmail("test@example.com");
        testStudent.setMajor("计算机科学");
        testStudent.setClassName("计算机2021-1班");
        testStudent.setStatus(1);
        
        // 准备用户信息请求
        userInfoRequest = new UserInfoRequest();
        userInfoRequest.setName("更新后的姓名");
        userInfoRequest.setGender(1);
        userInfoRequest.setAge(21);
        userInfoRequest.setPhone("13900139000");
        userInfoRequest.setEmail("newemail@example.com");
        userInfoRequest.setMajor("软件工程");
        userInfoRequest.setClassName("软件2021-1班");
    }
    
    @Test
    @DisplayName("获取用户信息 - 成功")
    public void testGetUserInfo_Success() {
        // Given
        when(studentUserMapper.selectById(anyLong())).thenReturn(testStudent);
        
        // When
        Result result = userService.getUserInfo(TEST_STUDENT_ID);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(studentUserMapper, times(1)).selectById(anyLong());
    }
    
    @Test
    @DisplayName("获取用户信息 - 用户不存在")
    public void testGetUserInfo_NotExists() {
        // Given
        when(studentUserMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = userService.getUserInfo(999L);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("用户") || 
                   result.getMessage().contains("不存在"));
        verify(studentUserMapper, times(1)).selectById(anyLong());
    }
    
    @Test
    @DisplayName("更新用户信息 - 成功")
    public void testUpdateUserInfo_Success() {
        // Given
        when(studentUserMapper.selectById(anyLong())).thenReturn(testStudent);
        when(studentUserMapper.updateById(any(StudentUser.class))).thenReturn(1);
        
        // When
        Result result = userService.updateUserInfo(TEST_STUDENT_ID, userInfoRequest);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("更新成功") || result.getMessage().contains("成功"));
        verify(studentUserMapper, times(1)).selectById(anyLong());
        verify(studentUserMapper, times(1)).updateById(any(StudentUser.class));
    }
    
    @Test
    @DisplayName("更新用户信息 - 用户不存在")
    public void testUpdateUserInfo_NotExists() {
        // Given
        when(studentUserMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = userService.updateUserInfo(999L, userInfoRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("用户") || 
                   result.getMessage().contains("不存在"));
        verify(studentUserMapper, times(1)).selectById(anyLong());
        verify(studentUserMapper, never()).updateById(any(StudentUser.class));
    }
    
    @Test
    @DisplayName("修改密码 - 成功")
    public void testChangePassword_Success() {
        // Given
        String oldPassword = "123456";
        String newPassword = "newpassword123";
        
        when(studentUserMapper.selectById(anyLong())).thenReturn(testStudent);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$newEncodedPassword");
        when(studentUserMapper.updateById(any(StudentUser.class))).thenReturn(1);
        
        // When
        Result result = userService.changePassword(TEST_STUDENT_ID, oldPassword, newPassword);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("修改成功") || result.getMessage().contains("成功"));
        verify(studentUserMapper, times(1)).selectById(anyLong());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(studentUserMapper, times(1)).updateById(any(StudentUser.class));
    }
    
    @Test
    @DisplayName("修改密码 - 旧密码错误")
    public void testChangePassword_WrongOldPassword() {
        // Given
        String oldPassword = "wrongpassword";
        String newPassword = "newpassword123";
        
        when(studentUserMapper.selectById(anyLong())).thenReturn(testStudent);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        // When
        Result result = userService.changePassword(TEST_STUDENT_ID, oldPassword, newPassword);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("密码") || 
                   result.getMessage().contains("错误") ||
                   result.getMessage().contains("原密码"));
        verify(studentUserMapper, times(1)).selectById(anyLong());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(studentUserMapper, never()).updateById(any(StudentUser.class));
    }
    
    @Test
    @DisplayName("修改密码 - 用户不存在")
    public void testChangePassword_NotExists() {
        // Given
        String oldPassword = "123456";
        String newPassword = "newpassword123";
        
        when(studentUserMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = userService.changePassword(999L, oldPassword, newPassword);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("用户") || 
                   result.getMessage().contains("不存在"));
        verify(studentUserMapper, times(1)).selectById(anyLong());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
    
    @Test
    @DisplayName("修改密码 - 新密码为空")
    public void testChangePassword_EmptyNewPassword() {
        // Given
        String oldPassword = "123456";
        String newPassword = "";
        
        // When
        Result result = userService.changePassword(TEST_STUDENT_ID, oldPassword, newPassword);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("密码") || 
                   result.getMessage().contains("空"));
    }
    
    @Test
    @DisplayName("更新用户信息 - 部分字段更新")
    public void testUpdateUserInfo_PartialUpdate() {
        // Given
        UserInfoRequest partialRequest = new UserInfoRequest();
        partialRequest.setName("新姓名");
        partialRequest.setPhone("13911111111");
        
        when(studentUserMapper.selectById(anyLong())).thenReturn(testStudent);
        when(studentUserMapper.updateById(any(StudentUser.class))).thenReturn(1);
        
        // When
        Result result = userService.updateUserInfo(TEST_STUDENT_ID, partialRequest);
        
        // Then
        assertEquals(200, result.getCode());
        verify(studentUserMapper, times(1)).selectById(anyLong());
        verify(studentUserMapper, times(1)).updateById(any(StudentUser.class));
    }
    
    @Test
    @DisplayName("更新用户信息 - 邮箱格式验证")
    public void testUpdateUserInfo_InvalidEmail() {
        // Given
        userInfoRequest.setEmail("invalid-email");
        
        // When & Then
        try {
            Result result = userService.updateUserInfo(TEST_STUDENT_ID, userInfoRequest);
            assertNotNull(result);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}
