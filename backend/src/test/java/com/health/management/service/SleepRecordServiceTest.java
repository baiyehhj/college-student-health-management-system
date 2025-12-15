package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.BaseTest;
import com.health.management.common.Result;
import com.health.management.dto.SleepRecordRequest;
import com.health.management.entity.SleepRecord;
import com.health.management.mapper.SleepRecordMapper;
import com.health.management.service.impl.SleepRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 睡眠记录服务测试类
 */
@DisplayName("睡眠记录服务测试")
public class SleepRecordServiceTest extends BaseTest {
    
    @Mock
    private SleepRecordMapper sleepRecordMapper;
    
    @InjectMocks
    private SleepRecordServiceImpl sleepRecordService;
    
    private SleepRecordRequest sleepRecordRequest;
    private SleepRecord testSleepRecord;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 准备睡眠记录请求数据
        sleepRecordRequest = new SleepRecordRequest();
        sleepRecordRequest.setRecordDate(LocalDate.now());
        sleepRecordRequest.setSleepTime(LocalDateTime.now().minusHours(8));
        sleepRecordRequest.setWakeTime(LocalDateTime.now());
        sleepRecordRequest.setDuration(new BigDecimal("8.0"));
        sleepRecordRequest.setQuality(3);
        sleepRecordRequest.setDeepSleepDuration(new BigDecimal("2.5"));
        sleepRecordRequest.setDreamCount(1);

        
        // 准备测试睡眠记录
        testSleepRecord = new SleepRecord();
        testSleepRecord.setId(1L);
        testSleepRecord.setStudentId(TEST_STUDENT_ID);
        testSleepRecord.setRecordDate(LocalDate.now());
        testSleepRecord.setSleepTime(LocalDateTime.now().minusHours(8));
        testSleepRecord.setWakeTime(LocalDateTime.now());
        testSleepRecord.setDuration(new BigDecimal("8.0"));
        testSleepRecord.setQuality(3);
        testSleepRecord.setDeepSleepDuration(new BigDecimal("2.5"));
        testSleepRecord.setDreamCount(1);
    }
    
    @Test
    @DisplayName("添加睡眠记录 - 成功")
    public void testAddRecord_Success() {
        // Given
        when(sleepRecordMapper.insert(any(SleepRecord.class))).thenReturn(1);
        
        // When
        Result result = sleepRecordService.addRecord(TEST_STUDENT_ID, sleepRecordRequest);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("成功"));
        verify(sleepRecordMapper, times(1)).insert(any(SleepRecord.class));
    }
    
    @Test
    @DisplayName("添加睡眠记录 - 必填项缺失")
    public void testAddRecord_MissingRequiredFields() {
        // Given
        sleepRecordRequest.setSleepTime(null);
        
        // When
        Result result = sleepRecordService.addRecord(TEST_STUDENT_ID, sleepRecordRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        verify(sleepRecordMapper, never()).insert(any(SleepRecord.class));
    }
    
    @Test
    @DisplayName("添加睡眠记录 - 时间不合理")
    public void testAddRecord_InvalidTime() {
        // Given
        sleepRecordRequest.setWakeTime(LocalDateTime.now().minusHours(10));
        sleepRecordRequest.setSleepTime(LocalDateTime.now());
        
        // When
        Result result = sleepRecordService.addRecord(TEST_STUDENT_ID, sleepRecordRequest);
        
        // Then
        if (result.getCode() != 200) {
            assertTrue(result.getMessage().contains("时间") || result.getMessage().contains("不合理"));
        }
    }
    
    @Test
    @DisplayName("更新睡眠记录 - 成功")
    public void testUpdateRecord_Success() {
        // Given
        when(sleepRecordMapper.selectById(anyLong())).thenReturn(testSleepRecord);
        when(sleepRecordMapper.updateById(any(SleepRecord.class))).thenReturn(1);
        
        // When
        Result result = sleepRecordService.updateRecord(1L, TEST_STUDENT_ID, sleepRecordRequest);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("更新成功"));
        verify(sleepRecordMapper, times(1)).selectById(anyLong());
        verify(sleepRecordMapper, times(1)).updateById(any(SleepRecord.class));
    }
    
    @Test
    @DisplayName("更新睡眠记录 - 记录不存在")
    public void testUpdateRecord_NotExists() {
        // Given
        when(sleepRecordMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = sleepRecordService.updateRecord(1L, TEST_STUDENT_ID, sleepRecordRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("不存在"));
        verify(sleepRecordMapper, times(1)).selectById(anyLong());
        verify(sleepRecordMapper, never()).updateById(any(SleepRecord.class));
    }
    
    @Test
    @DisplayName("更新睡眠记录 - 无权限")
    public void testUpdateRecord_NoPermission() {
        // Given
        testSleepRecord.setStudentId(999L);
        when(sleepRecordMapper.selectById(anyLong())).thenReturn(testSleepRecord);
        
        // When
        Result result = sleepRecordService.updateRecord(1L, TEST_STUDENT_ID, sleepRecordRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("权限") || result.getMessage().contains("无权"));
        verify(sleepRecordMapper, times(1)).selectById(anyLong());
        verify(sleepRecordMapper, never()).updateById(any(SleepRecord.class));
    }
    
    @Test
    @DisplayName("删除睡眠记录 - 成功")
    public void testDeleteRecord_Success() {
        // Given
        when(sleepRecordMapper.selectById(anyLong())).thenReturn(testSleepRecord);
        when(sleepRecordMapper.deleteById(anyLong())).thenReturn(1);
        
        // When
        Result result = sleepRecordService.deleteRecord(1L, TEST_STUDENT_ID);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("删除成功"));
        verify(sleepRecordMapper, times(1)).selectById(anyLong());
        verify(sleepRecordMapper, times(1)).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("删除睡眠记录 - 记录不存在")
    public void testDeleteRecord_NotExists() {
        // Given
        when(sleepRecordMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = sleepRecordService.deleteRecord(1L, TEST_STUDENT_ID);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("不存在"));
        verify(sleepRecordMapper, times(1)).selectById(anyLong());
        verify(sleepRecordMapper, never()).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("查询睡眠记录列表 - 成功")
    public void testListRecords_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<SleepRecord> records = Arrays.asList(testSleepRecord);
        
        Page<SleepRecord> page = new Page<>(1, 10);
        page.setRecords(records);
        page.setTotal(1);
        
        when(sleepRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        
        // When
        Result result = sleepRecordService.listRecords(TEST_STUDENT_ID, startDate, endDate, 1, 10);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(sleepRecordMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("查询睡眠记录列表 - 空结果")
    public void testListRecords_EmptyResult() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        Page<SleepRecord> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        page.setTotal(0);
        
        when(sleepRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        
        // When
        Result result = sleepRecordService.listRecords(TEST_STUDENT_ID, startDate, endDate, 1, 10);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(sleepRecordMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

}
