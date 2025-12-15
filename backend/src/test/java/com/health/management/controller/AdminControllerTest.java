package com.health.management.controller;

import com.health.management.BaseTest;
import com.health.management.HealthManagementApplication;
import com.health.management.common.Result;
import com.health.management.service.AdminService;
import com.health.management.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 管理员控制器测试类
 */
@SpringBootTest(classes = HealthManagementApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("管理员控制器测试")
public class AdminControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtUtil jwtUtil;

    private String adminToken;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        adminToken = "Bearer admin_token_123456";

        // Mock JWT工具类 - 管理员权限
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.getRoleFromToken(anyString())).thenReturn("ADMIN");
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
    }

    @Test
    @DisplayName("获取学生列表 - 成功")
    public void testGetStudentList_Success() throws Exception {
        // Given
        when(adminService.getStudentList(anyInt(), anyInt(), any(), any()))
                .thenReturn(Result.success("查询成功"));

        // When & Then
        mockMvc.perform(get("/admin/students")
                        .header("Authorization", adminToken)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取学生列表 - 带搜索关键词")
    public void testGetStudentList_WithKeyword() throws Exception {
        // Given
        when(adminService.getStudentList(anyInt(), anyInt(), eq("张三"), any()))
                .thenReturn(Result.success("查询成功"));

        // When & Then
        mockMvc.perform(get("/admin/students")
                        .header("Authorization", adminToken)
                        .param("page", "1")
                        .param("size", "10")
                        .param("keyword", "张三"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取学生列表 - 按状态筛选")
    public void testGetStudentList_WithStatus() throws Exception {
        // Given
        when(adminService.getStudentList(anyInt(), anyInt(), any(), eq(1)))
                .thenReturn(Result.success("查询成功"));

        // When & Then
        mockMvc.perform(get("/admin/students")
                        .header("Authorization", adminToken)
                        .param("page", "1")
                        .param("size", "10")
                        .param("status", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("获取学生详情 - 成功")
    public void testGetStudentDetail_Success() throws Exception {
        // Given
        when(adminService.getStudentDetail(eq(TEST_STUDENT_ID)))
                .thenReturn(Result.success("查询成功"));

        // When & Then
        mockMvc.perform(get("/admin/students/" + TEST_STUDENT_ID)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("更新学生状态 - 启用")
    public void testUpdateStudentStatus_Enable() throws Exception {
        // Given
        when(adminService.updateStudentStatus(eq(TEST_STUDENT_ID), eq(1)))
                .thenReturn(Result.success("更新成功"));

        // When & Then
        mockMvc.perform(put("/admin/students/" + TEST_STUDENT_ID + "/status")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("更新学生状态 - 禁用")
    public void testUpdateStudentStatus_Disable() throws Exception {
        // Given
        when(adminService.updateStudentStatus(eq(TEST_STUDENT_ID), eq(0)))
                .thenReturn(Result.success("更新成功"));

        // When & Then
        mockMvc.perform(put("/admin/students/" + TEST_STUDENT_ID + "/status")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":0}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("重置学生密码 - 成功")
    public void testResetStudentPassword_Success() throws Exception {
        // Given
        when(adminService.resetStudentPassword(eq(TEST_STUDENT_ID)))
                .thenReturn(Result.success("重置成功"));

        // When & Then
        mockMvc.perform(post("/admin/students/" + TEST_STUDENT_ID + "/reset-password")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("重置成功"));
    }

    @Test
    @DisplayName("获取系统统计 - 成功")
    public void testGetSystemStatistics_Success() throws Exception {
        // Given
        when(adminService.getSystemStatistics())
                .thenReturn(Result.success("统计数据"));

        // When & Then
        mockMvc.perform(get("/admin/statistics")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取健康趋势统计 - 成功")
    public void testGetHealthTrendStatistics_Success() throws Exception {
        // Given
        when(adminService.getHealthTrendStatistics())
                .thenReturn(Result.success("趋势数据"));

        // When & Then
        mockMvc.perform(get("/admin/statistics/trends")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取健康预警统计 - 成功")
    public void testGetHealthAlertStatistics_Success() throws Exception {
        // Given
        when(adminService.getHealthAlertStatistics())
                .thenReturn(Result.success("预警数据"));

        // When & Then
        mockMvc.perform(get("/admin/statistics/alerts")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("导出学生数据 - 全部")
    public void testExportStudentData_All() throws Exception {
        // Given
        doNothing().when(adminService).exportStudentData(any(), eq("all"));

        // When & Then
        mockMvc.perform(get("/admin/export/students")
                        .header("Authorization", adminToken)
                        .param("type", "all"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("导出学生数据 - 基本信息")
    public void testExportStudentData_Basic() throws Exception {
        // Given
        doNothing().when(adminService).exportStudentData(any(), eq("basic"));

        // When & Then
        mockMvc.perform(get("/admin/export/students")
                        .header("Authorization", adminToken)
                        .param("type", "basic"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("导出学生健康数据 - 成功")
    public void testExportStudentHealthData_Success() throws Exception {
        // Given
        doNothing().when(adminService).exportStudentHealthData(any(), eq(TEST_STUDENT_ID));

        // When & Then
        mockMvc.perform(get("/admin/export/students/" + TEST_STUDENT_ID + "/health")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("权限验证 - 缺少Token")
    public void testAccessDenied_MissingToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/students"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    @DisplayName("批量操作 - 更新多个学生状态")
    public void testBatchUpdateStatus() throws Exception {
        // Given
        when(adminService.updateStudentStatus(anyLong(), anyInt()))
                .thenReturn(Result.success("更新成功"));

        // When & Then
        for (long id = 1; id <= 3; id++) {
            mockMvc.perform(put("/admin/students/" + id + "/status")
                            .header("Authorization", adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"status\":0}"))
                    .andExpect(status().isOk());
        }

        verify(adminService, times(3)).updateStudentStatus(anyLong(), eq(0));
    }

    @Test
    @DisplayName("搜索功能 - 复杂查询")
    public void testComplexSearch() throws Exception {
        // Given
        when(adminService.getStudentList(anyInt(), anyInt(), anyString(), anyInt()))
                .thenReturn(Result.success("查询成功"));

        // When & Then
        mockMvc.perform(get("/admin/students")
                        .header("Authorization", adminToken)
                        .param("page", "1")
                        .param("size", "20")
                        .param("keyword", "计算机")
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}

