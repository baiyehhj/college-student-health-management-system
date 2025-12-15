package com.health.management.util;

import com.health.management.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 */
@DisplayName("JWT工具类测试")
public class JwtUtilTest extends BaseTest {
    
    private JwtUtil jwtUtil;
    
    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
        // 使用ReflectionTestUtils设置私有字段
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_JWT_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", TEST_JWT_EXPIRATION);
    }
    
    @Test
    @DisplayName("生成学生Token - 成功")
    public void testGenerateToken_StudentSuccess() {
        // When
        String token = jwtUtil.generateToken(TEST_STUDENT_ID, TEST_STUDENT_NO);
        
        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT格式验证
    }
    
    @Test
    @DisplayName("生成带角色Token - 成功")
    public void testGenerateToken_WithRoleSuccess() {
        // When
        String studentToken = jwtUtil.generateToken(TEST_STUDENT_ID, TEST_STUDENT_NO, "STUDENT");
        String adminToken = jwtUtil.generateToken(TEST_ADMIN_ID, TEST_EMPLOYEE_NO, "ADMIN");
        
        // Then
        assertNotNull(studentToken);
        assertNotNull(adminToken);
        assertFalse(studentToken.isEmpty());
        assertFalse(adminToken.isEmpty());
    }
    
    @Test
    @DisplayName("从Token中获取用户ID - 成功")
    public void testGetUserIdFromToken_Success() {
        // Given
        String token = jwtUtil.generateToken(TEST_STUDENT_ID, TEST_STUDENT_NO, "STUDENT");
        
        // When
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        // Then
        assertNotNull(userId);
        assertEquals(TEST_STUDENT_ID, userId);
    }
    
    @Test
    @DisplayName("从Token中获取学生ID - 成功")
    public void testGetStudentIdFromToken_Success() {
        // Given
        String token = jwtUtil.generateToken(TEST_STUDENT_ID, TEST_STUDENT_NO, "STUDENT");
        
        // When
        Long studentId = jwtUtil.getStudentIdFromToken(token);
        
        // Then
        assertNotNull(studentId);
        assertEquals(TEST_STUDENT_ID, studentId);
    }
    
    @Test
    @DisplayName("从Token中获取用户编号 - 成功")
    public void testGetUserNoFromToken_Success() {
        // Given
        String token = jwtUtil.generateToken(TEST_STUDENT_ID, TEST_STUDENT_NO, "STUDENT");
        
        // When
        String userNo = jwtUtil.getUserNoFromToken(token);
        
        // Then
        assertNotNull(userNo);
        assertEquals(TEST_STUDENT_NO, userNo);
    }
    
    @Test
    @DisplayName("从Token中获取学号 - 成功")
    public void testGetStudentNoFromToken_Success() {
        // Given
        String token = jwtUtil.generateToken(TEST_STUDENT_ID, TEST_STUDENT_NO, "STUDENT");
        
        // When
        String studentNo = jwtUtil.getStudentNoFromToken(token);
        
        // Then
        assertNotNull(studentNo);
        assertEquals(TEST_STUDENT_NO, studentNo);
    }
    
    @Test
    @DisplayName("从Token中获取角色 - 成功")
    public void testGetRoleFromToken_Success() {
        // Given
        String studentToken = jwtUtil.generateToken(TEST_STUDENT_ID, TEST_STUDENT_NO, "STUDENT");
        String adminToken = jwtUtil.generateToken(TEST_ADMIN_ID, TEST_EMPLOYEE_NO, "ADMIN");
        
        // When
        String studentRole = jwtUtil.getRoleFromToken(studentToken);
        String adminRole = jwtUtil.getRoleFromToken(adminToken);
        
        // Then
        assertEquals("STUDENT", studentRole);
        assertEquals("ADMIN", adminRole);
    }
    
    @Test
    @DisplayName("从Token中获取角色 - 默认值")
    public void testGetRoleFromToken_DefaultValue() {
        // Given - 使用旧版本的generateToken（不带role）
        String token = jwtUtil.generateToken(TEST_STUDENT_ID, TEST_STUDENT_NO);
        
        // When
        String role = jwtUtil.getRoleFromToken(token);
        
        // Then
        assertNotNull(role);
        // 对于没有role的token，应该返回默认值"STUDENT"
        assertEquals("STUDENT", role);
    }
    
    @Test
    @DisplayName("验证Token - 有效Token")
    public void testValidateToken_ValidToken() {
        // Given
        String token = jwtUtil.generateToken(TEST_STUDENT_ID, TEST_STUDENT_NO, "STUDENT");
        
        // When
        Boolean isValid = jwtUtil.validateToken(token);
        
        // Then
        assertTrue(isValid);
    }
    
    @Test
    @DisplayName("验证Token - 无效Token")
    public void testValidateToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";
        
        // When
        Boolean isValid = jwtUtil.validateToken(invalidToken);
        
        // Then
        assertFalse(isValid);
    }
    
    @Test
    @DisplayName("验证Token - 空Token")
    public void testValidateToken_NullToken() {
        // When
        Boolean isValid = jwtUtil.validateToken(null);
        
        // Then
        assertFalse(isValid);
    }
    
    @Test
    @DisplayName("从无效Token获取用户ID - 返回null")
    public void testGetUserIdFromToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";
        
        // When
        Long userId = jwtUtil.getUserIdFromToken(invalidToken);
        
        // Then
        assertNull(userId);
    }
    
    @Test
    @DisplayName("从无效Token获取用户编号 - 返回null")
    public void testGetUserNoFromToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";
        
        // When
        String userNo = jwtUtil.getUserNoFromToken(invalidToken);
        
        // Then
        assertNull(userNo);
    }
    
    @Test
    @DisplayName("从无效Token获取角色 - 返回null")
    public void testGetRoleFromToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";
        
        // When
        String role = jwtUtil.getRoleFromToken(invalidToken);
        
        // Then
        assertNull(role);
    }
    
    @Test
    @DisplayName("Token包含所有必要信息")
    public void testToken_ContainsAllInfo() {
        // Given
        String token = jwtUtil.generateToken(TEST_STUDENT_ID, TEST_STUDENT_NO, "STUDENT");
        
        // When & Then
        assertNotNull(jwtUtil.getUserIdFromToken(token));
        assertNotNull(jwtUtil.getUserNoFromToken(token));
        assertNotNull(jwtUtil.getRoleFromToken(token));
        assertNotNull(jwtUtil.getStudentIdFromToken(token));
        assertNotNull(jwtUtil.getStudentNoFromToken(token));
        assertTrue(jwtUtil.validateToken(token));
    }
}
