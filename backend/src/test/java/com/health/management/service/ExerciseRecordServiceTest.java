package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.BaseTest;
import com.health.management.common.Result;
import com.health.management.dto.ExerciseRecordRequest;
import com.health.management.entity.ExerciseRecord;
import com.health.management.mapper.ExerciseRecordMapper;
import com.health.management.service.impl.ExerciseRecordServiceImpl;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 运动记录服务测试类
 */
@DisplayName("运动记录服务测试")
public class ExerciseRecordServiceTest extends BaseTest {
    
    @Mock
    private ExerciseRecordMapper exerciseRecordMapper;
    
    @InjectMocks
    private ExerciseRecordServiceImpl exerciseRecordService;
    
    private ExerciseRecordRequest exerciseRecordRequest;
    private ExerciseRecord testExerciseRecord;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 准备运动记录请求数据
        exerciseRecordRequest = new ExerciseRecordRequest();
        exerciseRecordRequest.setRecordDate(LocalDate.now());
        exerciseRecordRequest.setExerciseType("跑步");
        exerciseRecordRequest.setDuration(30);
        exerciseRecordRequest.setCaloriesBurned(new BigDecimal("250.0"));
        exerciseRecordRequest.setDistance(new BigDecimal("5.0"));

        
        // 准备测试运动记录
        testExerciseRecord = new ExerciseRecord();
        testExerciseRecord.setId(1L);
        testExerciseRecord.setStudentId(TEST_STUDENT_ID);
        testExerciseRecord.setRecordDate(LocalDate.now());
        testExerciseRecord.setExerciseType("跑步");
        testExerciseRecord.setDuration(30);
        testExerciseRecord.setCaloriesBurned(new BigDecimal("250.0"));
        testExerciseRecord.setDistance(new BigDecimal("5.0"));
    }
    
    @Test
    @DisplayName("添加运动记录 - 成功")
    public void testAddRecord_Success() {
        // Given
        when(exerciseRecordMapper.insert(any(ExerciseRecord.class))).thenReturn(1);
        
        // When
        Result result = exerciseRecordService.addRecord(TEST_STUDENT_ID, exerciseRecordRequest);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("成功"));
        verify(exerciseRecordMapper, times(1)).insert(any(ExerciseRecord.class));
    }
    
    @Test
    @DisplayName("添加运动记录 - 必填项缺失")
    public void testAddRecord_MissingRequiredFields() {
        // Given
        exerciseRecordRequest.setExerciseType(null);
        
        // When
        Result result = exerciseRecordService.addRecord(TEST_STUDENT_ID, exerciseRecordRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        verify(exerciseRecordMapper, never()).insert(any(ExerciseRecord.class));
    }
    
    @Test
    @DisplayName("更新运动记录 - 成功")
    public void testUpdateRecord_Success() {
        // Given
        when(exerciseRecordMapper.selectById(anyLong())).thenReturn(testExerciseRecord);
        when(exerciseRecordMapper.updateById(any(ExerciseRecord.class))).thenReturn(1);
        
        // When
        Result result = exerciseRecordService.updateRecord(1L, TEST_STUDENT_ID, exerciseRecordRequest);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("更新成功"));
        verify(exerciseRecordMapper, times(1)).selectById(anyLong());
        verify(exerciseRecordMapper, times(1)).updateById(any(ExerciseRecord.class));
    }
    
    @Test
    @DisplayName("更新运动记录 - 记录不存在")
    public void testUpdateRecord_NotExists() {
        // Given
        when(exerciseRecordMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = exerciseRecordService.updateRecord(1L, TEST_STUDENT_ID, exerciseRecordRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("不存在"));
        verify(exerciseRecordMapper, times(1)).selectById(anyLong());
        verify(exerciseRecordMapper, never()).updateById(any(ExerciseRecord.class));
    }
    
    @Test
    @DisplayName("更新运动记录 - 无权限")
    public void testUpdateRecord_NoPermission() {
        // Given
        testExerciseRecord.setStudentId(999L);
        when(exerciseRecordMapper.selectById(anyLong())).thenReturn(testExerciseRecord);
        
        // When
        Result result = exerciseRecordService.updateRecord(1L, TEST_STUDENT_ID, exerciseRecordRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("权限") || result.getMessage().contains("无权"));
        verify(exerciseRecordMapper, times(1)).selectById(anyLong());
        verify(exerciseRecordMapper, never()).updateById(any(ExerciseRecord.class));
    }
    
    @Test
    @DisplayName("删除运动记录 - 成功")
    public void testDeleteRecord_Success() {
        // Given
        when(exerciseRecordMapper.selectById(anyLong())).thenReturn(testExerciseRecord);
        when(exerciseRecordMapper.deleteById(anyLong())).thenReturn(1);
        
        // When
        Result result = exerciseRecordService.deleteRecord(1L, TEST_STUDENT_ID);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("删除成功"));
        verify(exerciseRecordMapper, times(1)).selectById(anyLong());
        verify(exerciseRecordMapper, times(1)).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("删除运动记录 - 记录不存在")
    public void testDeleteRecord_NotExists() {
        // Given
        when(exerciseRecordMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = exerciseRecordService.deleteRecord(1L, TEST_STUDENT_ID);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("不存在"));
        verify(exerciseRecordMapper, times(1)).selectById(anyLong());
        verify(exerciseRecordMapper, never()).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("查询运动记录列表 - 成功")
    public void testListRecords_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<ExerciseRecord> records = Arrays.asList(testExerciseRecord);
        
        Page<ExerciseRecord> page = new Page<>(1, 10);
        page.setRecords(records);
        page.setTotal(1);
        
        when(exerciseRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        
        // When
        Result result = exerciseRecordService.listRecords(TEST_STUDENT_ID, startDate, endDate, 1, 10);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(exerciseRecordMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("查询运动记录列表 - 空结果")
    public void testListRecords_EmptyResult() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        Page<ExerciseRecord> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        page.setTotal(0);
        
        when(exerciseRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        
        // When
        Result result = exerciseRecordService.listRecords(TEST_STUDENT_ID, startDate, endDate, 1, 10);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(exerciseRecordMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

}
