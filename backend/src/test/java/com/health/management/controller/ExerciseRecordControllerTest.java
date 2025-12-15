package com.health.management.controller;

import com.health.management.BaseTest;
import com.health.management.HealthManagementApplication;
import com.health.management.common.Result;
import com.health.management.dto.ExerciseRecordRequest;
import com.health.management.service.ExerciseRecordService;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 运动记录控制器测试类
 */

@DisplayName("运动记录控制器测试")
@SpringBootTest(classes = HealthManagementApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ExerciseRecordControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExerciseRecordService exerciseRecordService;

    @MockBean
    private JwtUtil jwtUtil;

    private String testToken;
    private ExerciseRecordRequest exerciseRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testToken = "Bearer test_token_123456";

        // 准备运动记录请求数据
        exerciseRequest = new ExerciseRecordRequest();
        exerciseRequest.setExerciseType("跑步");
        exerciseRequest.setDuration(30);
        exerciseRequest.setCaloriesBurned(new BigDecimal("300"));
        exerciseRequest.setRecordDate(LocalDate.now());

        // Mock JWT工具类
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.getStudentIdFromToken(anyString())).thenReturn(TEST_STUDENT_ID);
    }

    @Test
    @DisplayName("添加运动记录 - 成功")
    public void testAddRecord_Success() throws Exception {
        // Given
        when(exerciseRecordService.addRecord(eq(TEST_STUDENT_ID), any(ExerciseRecordRequest.class)))
                .thenReturn(Result.success("添加成功"));

        // When & Then
        mockMvc.perform(post("/exercise/add")
                        .header("Authorization", testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"exerciseType\":\"跑步\",\"duration\":45,\"calories\":250,\"recordDate\":\"2025-01-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(exerciseRecordService, times(1))
                .addRecord(eq(TEST_STUDENT_ID), any(ExerciseRecordRequest.class));
    }

    @Test
    @DisplayName("添加运动记录 - 缺少Token")
    public void testAddRecord_MissingToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/exercise/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"exerciseType\":\"跑步\",\"duration\":30}"))
                .andExpect(status().is4xxClientError());

        verify(exerciseRecordService, never()).addRecord(any(), any());
    }

    @Test
    @DisplayName("添加运动记录 - 参数验证失败")
    public void testAddRecord_ValidationFailed() throws Exception {
        // When & Then - 运动类型为空
        mockMvc.perform(post("/exercise/add")
                        .header("Authorization", testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"duration\":30,\"calories\":200}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("更新运动记录 - 成功")
    public void testUpdateRecord_Success() throws Exception {
        // Given
        when(exerciseRecordService.updateRecord(eq(1L), eq(TEST_STUDENT_ID), any(ExerciseRecordRequest.class)))
                .thenReturn(Result.success("更新成功"));

        // When & Then
        mockMvc.perform(put("/exercise/update/1")
                        .header("Authorization", testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"exerciseType\":\"跑步\",\"duration\":45,\"calories\":250,\"recordDate\":\"2025-01-01\"} "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除运动记录 - 成功")
    public void testDeleteRecord_Success() throws Exception {
        // Given
        when(exerciseRecordService.deleteRecord(eq(1L), eq(TEST_STUDENT_ID)))
                .thenReturn(Result.success("删除成功"));

        // When & Then
        mockMvc.perform(delete("/exercise/delete/1")
                        .header("Authorization", testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }


}
