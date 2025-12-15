package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.BaseTest;
import com.health.management.common.Result;
import com.health.management.dto.DietRecordRequest;
import com.health.management.entity.DietRecord;
import com.health.management.mapper.DietRecordMapper;
import com.health.management.service.impl.DietRecordServiceImpl;
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
 * 饮食记录服务测试类
 */
@DisplayName("饮食记录服务测试")
public class DietRecordServiceTest extends BaseTest {
    
    @Mock
    private DietRecordMapper dietRecordMapper;
    
    @InjectMocks
    private DietRecordServiceImpl dietRecordService;
    
    private DietRecordRequest dietRecordRequest;
    private DietRecord testDietRecord;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 准备饮食记录请求数据
        dietRecordRequest = new DietRecordRequest();
        dietRecordRequest.setRecordDate(LocalDate.now());
        dietRecordRequest.setMealType(1); // 早餐
        dietRecordRequest.setFoodName("牛奶面包");
        dietRecordRequest.setFoodCategory("主食");
        dietRecordRequest.setCalories(new BigDecimal("300.0"));
        dietRecordRequest.setProtein(new BigDecimal("12.0"));
        dietRecordRequest.setCarbs(new BigDecimal("50.0"));
        dietRecordRequest.setFat(new BigDecimal("8.0"));

        
        // 准备测试饮食记录
        testDietRecord = new DietRecord();
        testDietRecord.setId(1L);
        testDietRecord.setStudentId(TEST_STUDENT_ID);
        testDietRecord.setRecordDate(LocalDate.now());
        testDietRecord.setMealType(1);
        testDietRecord.setFoodName("牛奶面包");
        testDietRecord.setFoodCategory("主食");
        testDietRecord.setCalories(new BigDecimal("300.0"));
        testDietRecord.setProtein(new BigDecimal("12.0"));
        testDietRecord.setCarbs(new BigDecimal("50.0"));
        testDietRecord.setFat(new BigDecimal("8.0"));
    }
    
    @Test
    @DisplayName("添加饮食记录 - 成功")
    public void testAddRecord_Success() {
        // Given
        when(dietRecordMapper.insert(any(DietRecord.class))).thenReturn(1);
        
        // When
        Result result = dietRecordService.addRecord(TEST_STUDENT_ID, dietRecordRequest);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("成功"));
        verify(dietRecordMapper, times(1)).insert(any(DietRecord.class));
    }
    
    @Test
    @DisplayName("添加饮食记录 - 必填项缺失")
    public void testAddRecord_MissingRequiredFields() {
        // Given
        dietRecordRequest.setRecordDate(null);
        
        // When
        Result result = dietRecordService.addRecord(TEST_STUDENT_ID, dietRecordRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        verify(dietRecordMapper, never()).insert(any(DietRecord.class));
    }
    
    @Test
    @DisplayName("更新饮食记录 - 成功")
    public void testUpdateRecord_Success() {
        // Given
        when(dietRecordMapper.selectById(anyLong())).thenReturn(testDietRecord);
        when(dietRecordMapper.updateById(any(DietRecord.class))).thenReturn(1);
        
        // When
        Result result = dietRecordService.updateRecord(1L, TEST_STUDENT_ID, dietRecordRequest);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("更新成功"));
        verify(dietRecordMapper, times(1)).selectById(anyLong());
        verify(dietRecordMapper, times(1)).updateById(any(DietRecord.class));
    }
    
    @Test
    @DisplayName("更新饮食记录 - 记录不存在")
    public void testUpdateRecord_NotExists() {
        // Given
        when(dietRecordMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = dietRecordService.updateRecord(1L, TEST_STUDENT_ID, dietRecordRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("不存在"));
        verify(dietRecordMapper, times(1)).selectById(anyLong());
        verify(dietRecordMapper, never()).updateById(any(DietRecord.class));
    }
    
    @Test
    @DisplayName("更新饮食记录 - 无权限")
    public void testUpdateRecord_NoPermission() {
        // Given
        testDietRecord.setStudentId(999L); // 不同的学生ID
        when(dietRecordMapper.selectById(anyLong())).thenReturn(testDietRecord);
        
        // When
        Result result = dietRecordService.updateRecord(1L, TEST_STUDENT_ID, dietRecordRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("权限") || result.getMessage().contains("无权"));
        verify(dietRecordMapper, times(1)).selectById(anyLong());
        verify(dietRecordMapper, never()).updateById(any(DietRecord.class));
    }
    
    @Test
    @DisplayName("删除饮食记录 - 成功")
    public void testDeleteRecord_Success() {
        // Given
        when(dietRecordMapper.selectById(anyLong())).thenReturn(testDietRecord);
        when(dietRecordMapper.deleteById(anyLong())).thenReturn(1);
        
        // When
        Result result = dietRecordService.deleteRecord(1L, TEST_STUDENT_ID);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("删除成功"));
        verify(dietRecordMapper, times(1)).selectById(anyLong());
        verify(dietRecordMapper, times(1)).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("删除饮食记录 - 记录不存在")
    public void testDeleteRecord_NotExists() {
        // Given
        when(dietRecordMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = dietRecordService.deleteRecord(1L, TEST_STUDENT_ID);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("不存在"));
        verify(dietRecordMapper, times(1)).selectById(anyLong());
        verify(dietRecordMapper, never()).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("删除饮食记录 - 无权限")
    public void testDeleteRecord_NoPermission() {
        // Given
        testDietRecord.setStudentId(999L);
        when(dietRecordMapper.selectById(anyLong())).thenReturn(testDietRecord);
        
        // When
        Result result = dietRecordService.deleteRecord(1L, TEST_STUDENT_ID);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("权限") || result.getMessage().contains("无权"));
        verify(dietRecordMapper, times(1)).selectById(anyLong());
        verify(dietRecordMapper, never()).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("查询饮食记录列表 - 成功")
    public void testListRecords_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<DietRecord> records = Arrays.asList(testDietRecord);
        
        Page<DietRecord> page = new Page<>(1, 10);
        page.setRecords(records);
        page.setTotal(1);
        
        when(dietRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        
        // When
        Result result = dietRecordService.listRecords(TEST_STUDENT_ID, startDate, endDate, 1, 10);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(dietRecordMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("查询饮食记录列表 - 空结果")
    public void testListRecords_EmptyResult() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        Page<DietRecord> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        page.setTotal(0);
        
        when(dietRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        
        // When
        Result result = dietRecordService.listRecords(TEST_STUDENT_ID, startDate, endDate, 1, 10);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(dietRecordMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取每日统计 - 成功")
    public void testGetDailyStats_Success() {
        // Given
        LocalDate date = LocalDate.now();
        List<DietRecord> records = Arrays.asList(testDietRecord);
        
        when(dietRecordMapper.selectList(any(QueryWrapper.class))).thenReturn(records);
        
        // When
        Result result = dietRecordService.getDailyStats(TEST_STUDENT_ID, date);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(dietRecordMapper, times(1)).selectList(any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取每日统计 - 无记录")
    public void testGetDailyStats_NoRecords() {
        // Given
        LocalDate date = LocalDate.now();
        when(dietRecordMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
        
        // When
        Result result = dietRecordService.getDailyStats(TEST_STUDENT_ID, date);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(dietRecordMapper, times(1)).selectList(any(QueryWrapper.class));
    }

}
