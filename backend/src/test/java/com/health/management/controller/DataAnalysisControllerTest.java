package com.health.management.controller;

import com.health.management.BaseTest;
import com.health.management.HealthManagementApplication;
import com.health.management.common.Result;
import com.health.management.service.DataAnalysisService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 数据分析控制器测试类
 */
@SpringBootTest(classes = HealthManagementApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("数据分析控制器测试")
public class DataAnalysisControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataAnalysisService dataAnalysisService;

    @MockBean
    private JwtUtil jwtUtil;

    private String testToken;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testToken = "Bearer test_token_123456";
        
        // Mock JWT工具类
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.getStudentIdFromToken(anyString())).thenReturn(TEST_STUDENT_ID);
    }

    @Test
    @DisplayName("获取体重趋势 - 7天")
    public void testGetWeightTrend_7Days() throws Exception {
        // Given
        when(dataAnalysisService.getWeightTrend(eq(TEST_STUDENT_ID), eq(7)))
                .thenReturn(Result.success("趋势数据"));

        // When & Then
        mockMvc.perform(get("/analysis/weight-trend")
                        .header("Authorization", testToken)
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取体重趋势 - 30天")
    public void testGetWeightTrend_30Days() throws Exception {
        // Given
        when(dataAnalysisService.getWeightTrend(eq(TEST_STUDENT_ID), eq(30)))
                .thenReturn(Result.success("趋势数据"));

        // When & Then
        mockMvc.perform(get("/analysis/weight-trend")
                        .header("Authorization", testToken)
                        .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取体重趋势 - 默认天数")
    public void testGetWeightTrend_DefaultDays() throws Exception {
        // Given
        when(dataAnalysisService.getWeightTrend(eq(TEST_STUDENT_ID), anyInt()))
                .thenReturn(Result.success("趋势数据"));

        // When & Then
        mockMvc.perform(get("/analysis/weight-trend")
                        .header("Authorization", testToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("获取运动统计 - 成功")
    public void testGetExerciseStats_Success() throws Exception {
        // Given
        when(dataAnalysisService.getExerciseStats(eq(TEST_STUDENT_ID), eq(7)))
                .thenReturn(Result.success("统计数据"));

        // When & Then
        mockMvc.perform(get("/analysis/exercise-stats")
                        .header("Authorization", testToken)
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取运动统计 - 不同时间范围")
    public void testGetExerciseStats_DifferentRanges() throws Exception {
        // Given
        when(dataAnalysisService.getExerciseStats(eq(TEST_STUDENT_ID), anyInt()))
                .thenReturn(Result.success("统计数据"));

        // When & Then - 7天
        mockMvc.perform(get("/analysis/exercise-stats")
                        .header("Authorization", testToken)
                        .param("days", "7"))
                .andExpect(status().isOk());

        // When & Then - 30天
        mockMvc.perform(get("/analysis/exercise-stats")
                        .header("Authorization", testToken)
                        .param("days", "30"))
                .andExpect(status().isOk());

        // When & Then - 90天
        mockMvc.perform(get("/analysis/exercise-stats")
                        .header("Authorization", testToken)
                        .param("days", "90"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("获取睡眠趋势 - 成功")
    public void testGetSleepTrend_Success() throws Exception {
        // Given
        when(dataAnalysisService.getSleepTrend(eq(TEST_STUDENT_ID), eq(7)))
                .thenReturn(Result.success("趋势数据"));

        // When & Then
        mockMvc.perform(get("/analysis/sleep-trend")
                        .header("Authorization", testToken)
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取睡眠趋势 - 长期趋势")
    public void testGetSleepTrend_LongTerm() throws Exception {
        // Given
        when(dataAnalysisService.getSleepTrend(eq(TEST_STUDENT_ID), eq(365)))
                .thenReturn(Result.success("趋势数据"));

        // When & Then
        mockMvc.perform(get("/analysis/sleep-trend")
                        .header("Authorization", testToken)
                        .param("days", "365"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("获取心情分布 - 成功")
    public void testGetMoodDistribution_Success() throws Exception {
        // Given
        when(dataAnalysisService.getMoodDistribution(eq(TEST_STUDENT_ID), eq(7)))
                .thenReturn(Result.success("分布数据"));

        // When & Then
        mockMvc.perform(get("/analysis/mood-distribution")
                        .header("Authorization", testToken)
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取心情分布 - 月度统计")
    public void testGetMoodDistribution_Monthly() throws Exception {
        // Given
        when(dataAnalysisService.getMoodDistribution(eq(TEST_STUDENT_ID), eq(30)))
                .thenReturn(Result.success("分布数据"));

        // When & Then
        mockMvc.perform(get("/analysis/mood-distribution")
                        .header("Authorization", testToken)
                        .param("days", "30"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("获取营养统计 - 成功")
    public void testGetNutritionStats_Success() throws Exception {
        // Given
        when(dataAnalysisService.getNutritionStats(eq(TEST_STUDENT_ID), eq(7)))
                .thenReturn(Result.success("营养数据"));

        // When & Then
        mockMvc.perform(get("/analysis/nutrition-stats")
                        .header("Authorization", testToken)
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取营养统计 - 周度对比")
    public void testGetNutritionStats_WeeklyComparison() throws Exception {
        // Given
        when(dataAnalysisService.getNutritionStats(eq(TEST_STUDENT_ID), anyInt()))
                .thenReturn(Result.success("营养数据"));

        // When & Then
        mockMvc.perform(get("/analysis/nutrition-stats")
                        .header("Authorization", testToken)
                        .param("days", "7"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/analysis/nutrition-stats")
                        .header("Authorization", testToken)
                        .param("days", "14"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("综合数据分析 - 获取所有维度")
    public void testComprehensiveAnalysis() throws Exception {
        // Given
        when(dataAnalysisService.getWeightTrend(any(), any()))
                .thenReturn(Result.success("体重数据"));
        when(dataAnalysisService.getExerciseStats(any(), any()))
                .thenReturn(Result.success("运动数据"));
        when(dataAnalysisService.getSleepTrend(any(), any()))
                .thenReturn(Result.success("睡眠数据"));
        when(dataAnalysisService.getMoodDistribution(any(), any()))
                .thenReturn(Result.success("心情数据"));
        when(dataAnalysisService.getNutritionStats(any(), any()))
                .thenReturn(Result.success("营养数据"));

        // When & Then - 依次获取所有数据
        mockMvc.perform(get("/analysis/weight-trend")
                        .header("Authorization", testToken)
                        .param("days", "7"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/analysis/exercise-stats")
                        .header("Authorization", testToken)
                        .param("days", "7"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/analysis/sleep-trend")
                        .header("Authorization", testToken)
                        .param("days", "7"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/analysis/mood-distribution")
                        .header("Authorization", testToken)
                        .param("days", "7"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/analysis/nutrition-stats")
                        .header("Authorization", testToken)
                        .param("days", "7"))
                .andExpect(status().isOk());

        verify(dataAnalysisService, times(1)).getWeightTrend(any(), any());
        verify(dataAnalysisService, times(1)).getExerciseStats(any(), any());
        verify(dataAnalysisService, times(1)).getSleepTrend(any(), any());
        verify(dataAnalysisService, times(1)).getMoodDistribution(any(), any());
        verify(dataAnalysisService, times(1)).getNutritionStats(any(), any());
    }


    @Test
    @DisplayName("Token验证 - 缺少Token")
    public void testMissingToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/analysis/weight-trend")
                        .param("days", "7"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    @DisplayName("边界条件 - 最大天数")
    public void testMaxDaysParameter() throws Exception {
        // Given
        when(dataAnalysisService.getWeightTrend(any(), eq(365)))
                .thenReturn(Result.success("数据"));

        // When & Then
        mockMvc.perform(get("/analysis/weight-trend")
                        .header("Authorization", testToken)
                        .param("days", "365"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("并发请求测试")
    public void testConcurrentRequests() throws Exception {
        // Given
        when(dataAnalysisService.getExerciseStats(any(), any()))
                .thenReturn(Result.success("数据"));

        // When & Then - 模拟多个并发请求
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/analysis/exercise-stats")
                            .header("Authorization", testToken)
                            .param("days", "7"))
                    .andExpect(status().isOk());
        }

        verify(dataAnalysisService, times(5)).getExerciseStats(any(), any());
    }

}
