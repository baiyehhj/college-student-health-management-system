
package com.health.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.management.BaseTest;
import com.health.management.HealthManagementApplication;
import com.health.management.common.Result;
import com.health.management.dto.DietRecordRequest;
import com.health.management.service.DietRecordService;
import com.health.management.util.JwtUtil;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 饮食记录控制器测试类
 */
@SpringBootTest(classes = HealthManagementApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("饮食记录控制器测试")
public class DietRecordControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DietRecordService dietRecordService;

    @MockBean
    private JwtUtil jwtUtil;

    private DietRecordRequest dietRecordRequest;
    private String testToken;

    @BeforeEach
    public void setUp() {
        testToken = "Bearer test.jwt.token";

        // 准备饮食记录请求数据
        dietRecordRequest = new DietRecordRequest();
        dietRecordRequest.setRecordDate(LocalDate.now());
        dietRecordRequest.setMealType(1);
        dietRecordRequest.setFoodName("牛奶面包");
        dietRecordRequest.setFoodCategory("主食");
        dietRecordRequest.setCalories(new BigDecimal("300.0"));
        dietRecordRequest.setProtein(new BigDecimal("12.0"));
        dietRecordRequest.setCarbs(new BigDecimal("50.0"));
        dietRecordRequest.setFat(new BigDecimal("8.0"));

        // Mock JWT工具类
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(TEST_STUDENT_ID);
    }

    @Test
    @DisplayName("添加饮食记录 - 成功")
    public void testAddRecord_Success() throws Exception {
        // Given
        when(dietRecordService.addRecord(anyLong(), any(DietRecordRequest.class)))
                .thenReturn(Result.success("添加成功"));

        // When & Then
        mockMvc.perform(post("/diet/add")
                .header("Authorization", testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dietRecordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("添加成功"));
    }

    @Test
    @DisplayName("添加饮食记录 - 参数校验失败")
    public void testAddRecord_ValidationFailed() throws Exception {
        // Given - 创建无效的请求（缺少必填字段）
        DietRecordRequest invalidRequest = new DietRecordRequest();

        // When & Then
        mockMvc.perform(post("/diet/add")
                .header("Authorization", testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("更新饮食记录 - 成功")
    public void testUpdateRecord_Success() throws Exception {
        // Given
        Long recordId = 1L;
        when(dietRecordService.updateRecord(anyLong(), anyLong(), any(DietRecordRequest.class)))
                .thenReturn(Result.success("更新成功"));

        // When & Then
        mockMvc.perform(put("/diet/update/" + recordId)
                .header("Authorization", testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dietRecordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    @DisplayName("删除饮食记录 - 成功")
    public void testDeleteRecord_Success() throws Exception {
        // Given
        Long recordId = 1L;
        when(dietRecordService.deleteRecord(anyLong(), anyLong()))
                .thenReturn(Result.success("删除成功"));

        // When & Then
        mockMvc.perform(delete("/diet/delete/" + recordId)
                .header("Authorization", testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    @DisplayName("查询饮食记录列表 - 成功")
    public void testListRecords_Success() throws Exception {
        // Given
        when(dietRecordService.listRecords(
                anyLong(),
                any(LocalDate.class),
                any(LocalDate.class),
                anyInt(),
                anyInt()))
                .thenReturn(Result.success("查询成功", null));

        // When & Then
        mockMvc.perform(get("/diet/list")
                .header("Authorization", testToken)
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-31")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取每日统计 - 成功")
    public void testGetDailyStats_Success() throws Exception {
        // Given
        when(dietRecordService.getDailyStats(anyLong(), any(LocalDate.class)))
                .thenReturn(Result.success("查询成功", null));

        // When & Then
        mockMvc.perform(get("/diet/stats/daily")
                .header("Authorization", testToken)
                .param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("查询饮食记录 - 缺少Token")
    public void testListRecords_MissingToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/diet/list")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-31")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }


}

