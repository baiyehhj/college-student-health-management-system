package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.management.BaseTest;
import com.health.management.common.Result;
import com.health.management.entity.*;
import com.health.management.mapper.*;
import com.health.management.service.impl.DataAnalysisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 数据分析服务测试类
 */
@DisplayName("数据分析服务测试")
public class DataAnalysisServiceTest extends BaseTest {
    
    @Mock
    private HealthExaminationMapper healthExaminationMapper;
    
    @Mock
    private ExerciseRecordMapper exerciseRecordMapper;
    
    @Mock
    private SleepRecordMapper sleepRecordMapper;
    
    @Mock
    private MoodRecordMapper moodRecordMapper;
    
    @Mock
    private DietRecordMapper dietRecordMapper;
    
    @InjectMocks
    private DataAnalysisServiceImpl dataAnalysisService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("获取体重趋势 - 7天")
    public void testGetWeightTrend_7Days() {
        // Given
        Integer days = 7;
        
        HealthExamination exam = new HealthExamination();
        exam.setExamDate(LocalDate.now());
        exam.setWeight(new BigDecimal("65.5"));
        
        when(healthExaminationMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(exam));
        
        // When
        Result result = dataAnalysisService.getWeightTrend(TEST_STUDENT_ID, days);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(healthExaminationMapper, times(1)).selectList(any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取体重趋势 - 30天")
    public void testGetWeightTrend_30Days() {
        // Given
        Integer days = 30;
        
        when(healthExaminationMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        
        // When
        Result result = dataAnalysisService.getWeightTrend(TEST_STUDENT_ID, days);
        
        // Then
        assertEquals(200, result.getCode());
        verify(healthExaminationMapper, times(1)).selectList(any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取运动统计 - 7天")
    public void testGetExerciseStats_Success() {
        // Given
        Integer days = 7;
        
        ExerciseRecord record = new ExerciseRecord();
        record.setRecordDate(LocalDate.now());
        record.setExerciseType("跑步");
        record.setDuration(30);
        record.setCaloriesBurned(new BigDecimal("250.0"));
        
        when(exerciseRecordMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(record));
        
        // When
        Result result = dataAnalysisService.getExerciseStats(TEST_STUDENT_ID, days);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(exerciseRecordMapper, times(1)).selectList(any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取运动统计 - 无数据")
    public void testGetExerciseStats_NoData() {
        // Given
        Integer days = 7;
        
        when(exerciseRecordMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        
        // When
        Result result = dataAnalysisService.getExerciseStats(TEST_STUDENT_ID, days);
        
        // Then
        assertEquals(200, result.getCode());
        verify(exerciseRecordMapper, times(1)).selectList(any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取睡眠趋势 - 7天")
    public void testGetSleepTrend_Success() {
        // Given
        Integer days = 7;
        
        SleepRecord record = new SleepRecord();
        record.setRecordDate(LocalDate.now());
        record.setDuration(new BigDecimal("8.0"));
        record.setQuality(3);
        
        when(sleepRecordMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(record));
        
        // When
        Result result = dataAnalysisService.getSleepTrend(TEST_STUDENT_ID, days);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(sleepRecordMapper, times(1)).selectList(any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取睡眠趋势 - 无数据")
    public void testGetSleepTrend_NoData() {
        // Given
        Integer days = 7;
        
        when(sleepRecordMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        
        // When
        Result result = dataAnalysisService.getSleepTrend(TEST_STUDENT_ID, days);
        
        // Then
        assertEquals(200, result.getCode());
        verify(sleepRecordMapper, times(1)).selectList(any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取情绪分布 - 7天")
    public void testGetMoodDistribution_Success() {
        // Given
        Integer days = 7;
        
        MoodRecord record = new MoodRecord();
        record.setRecordDate(LocalDate.now());
        record.setMoodType(1); // 开心
        record.setMoodScore(8);
        
        when(moodRecordMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(record));
        
        // When
        Result result = dataAnalysisService.getMoodDistribution(TEST_STUDENT_ID, days);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(moodRecordMapper, times(1)).selectList(any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取情绪分布 - 多种情绪")
    public void testGetMoodDistribution_MultipleTypes() {
        // Given
        Integer days = 7;
        
        MoodRecord record1 = new MoodRecord();
        record1.setMoodType(1); // 开心
        
        MoodRecord record2 = new MoodRecord();
        record2.setMoodType(3); // 焦虑
        
        when(moodRecordMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(record1, record2));
        
        // When
        Result result = dataAnalysisService.getMoodDistribution(TEST_STUDENT_ID, days);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(moodRecordMapper, times(1)).selectList(any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取营养统计 - 7天")
    public void testGetNutritionStats_Success() {
        // Given
        Integer days = 7;
        
        DietRecord record = new DietRecord();
        record.setRecordDate(LocalDate.now());
        record.setMealType(1);
        record.setCalories(new BigDecimal("500.0"));
        record.setProtein(new BigDecimal("20.0"));
        record.setCarbs(new BigDecimal("60.0"));
        record.setFat(new BigDecimal("15.0"));
        
        when(dietRecordMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(record));
        
        // When
        Result result = dataAnalysisService.getNutritionStats(TEST_STUDENT_ID, days);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(dietRecordMapper, times(1)).selectList(any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取营养统计 - 无数据")
    public void testGetNutritionStats_NoData() {
        // Given
        Integer days = 7;
        
        when(dietRecordMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        
        // When
        Result result = dataAnalysisService.getNutritionStats(TEST_STUDENT_ID, days);
        
        // Then
        assertEquals(200, result.getCode());
        verify(dietRecordMapper, times(1)).selectList(any(QueryWrapper.class));
    }

}
