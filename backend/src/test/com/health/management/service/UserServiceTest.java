package com.health.management.service;

import com.health.management.common.Result;
import com.health.management.dto.UserInfoRequest;
import com.health.management.entity.StudentUser;
import com.health.management.mapper.StudentUserMapper;
import com.health.management.service.impl.UserServiceImpl;
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
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
public class UserServiceTest {

    @Mock
    private StudentUserMapper studentUserMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private StudentUser validUser;
    private Long studentId;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        studentId = 1L;
        passwordEncoder = new BCryptPasswordEncoder();

        validUser = new StudentUser();
        validUser.setId(studentId);
        validUser.setStudentNo("2021001");
        validUser.setPassword(passwordEncoder.encode("password123"));
        validUser.setName("张三");
        validUser.setGender(1);
        validUser.setAge(20);
        validUser.setPhone("13800138000");
        validUser.setEmail("zhangsan@example.com");
        validUser.setMajor("计算机科学");
        validUser.setClassName("计科2101");
        validUser.setStatus(1);
    }

    @Test
    @DisplayName("获取用户信息-成功")
    void testGetUserInfo_Success() {
        when(studentUserMapper.selectById(studentId)).thenReturn(validUser);

        Result result = userService.getUserInfo(studentId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(studentUserMapper, times(1)).selectById(studentId);
    }

    @Test
    @DisplayName("获取用户信息-用户不存在")
    void testGetUserInfo_UserNotFound() {
        when(studentUserMapper.selectById(999L)).thenReturn(null);

        Result result = userService.getUserInfo(999L);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("不存在"));
    }

    @Test
    @DisplayName("更新用户信息-成功")
    void testUpdateUserInfo_Success() {
        UserInfoRequest request = new UserInfoRequest();
        request.setName("李四");
        request.setGender(1);
        request.setAge(21);
        request.setPhone("13900139000");
        request.setEmail("lisi@example.com");

        when(studentUserMapper.selectById(studentId)).thenReturn(validUser);
        when(studentUserMapper.updateById(any(StudentUser.class))).thenReturn(1);

        Result result = userService.updateUserInfo(studentId, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(studentUserMapper, times(1)).selectById(studentId);
        verify(studentUserMapper, times(1)).updateById(any(StudentUser.class));
    }

    @Test
    @DisplayName("更新用户信息-用户不存在")
    void testUpdateUserInfo_UserNotFound() {
        UserInfoRequest request = new UserInfoRequest();
        request.setName("李四");

        when(studentUserMapper.selectById(999L)).thenReturn(null);

        Result result = userService.updateUserInfo(999L, request);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        verify(studentUserMapper, never()).updateById(any(StudentUser.class));
    }

    @Test
    @DisplayName("修改密码-成功")
    void testChangePassword_Success() {
        String oldPassword = "password123";
        String newPassword = "newpassword456";

        when(studentUserMapper.selectById(studentId)).thenReturn(validUser);
        when(studentUserMapper.updateById(any(StudentUser.class))).thenReturn(1);

        Result result = userService.changePassword(studentId, oldPassword, newPassword);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(studentUserMapper, times(1)).selectById(studentId);
        verify(studentUserMapper, times(1)).updateById(any(StudentUser.class));
    }

    @Test
    @DisplayName("修改密码-旧密码错误")
    void testChangePassword_WrongOldPassword() {
        String wrongOldPassword = "wrongpassword";
        String newPassword = "newpassword456";

        when(studentUserMapper.selectById(studentId)).thenReturn(validUser);

        Result result = userService.changePassword(studentId, wrongOldPassword, newPassword);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("密码"));
        verify(studentUserMapper, never()).updateById(any(StudentUser.class));
    }

    @Test
    @DisplayName("修改密码-用户不存在")
    void testChangePassword_UserNotFound() {
        String oldPassword = "password123";
        String newPassword = "newpassword456";

        when(studentUserMapper.selectById(999L)).thenReturn(null);

        Result result = userService.changePassword(999L, oldPassword, newPassword);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        verify(studentUserMapper, never()).updateById(any(StudentUser.class));
    }

    @Test
    @DisplayName("更新用户信息-更新头像")
    void testUpdateUserInfo_WithAvatar() {
        UserInfoRequest request = new UserInfoRequest();
        request.setName("张三");
        request.setAvatar("http://example.com/avatar.jpg");

        when(studentUserMapper.selectById(studentId)).thenReturn(validUser);
        when(studentUserMapper.updateById(any(StudentUser.class))).thenReturn(1);

        Result result = userService.updateUserInfo(studentId, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(studentUserMapper, times(1)).updateById(any(StudentUser.class));
    }
}
