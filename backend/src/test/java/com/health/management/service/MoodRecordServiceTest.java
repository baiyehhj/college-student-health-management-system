package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.BaseTest;
import com.health.management.common.Result;
import com.health.management.dto.MoodRecordRequest;
import com.health.management.entity.MoodRecord;
import com.health.management.mapper.MoodRecordMapper;
import com.health.management.service.impl.MoodRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
 * 情绪记录服务测试类
 */
@DisplayName("情绪记录服务测试")
public class MoodRecordServiceTest extends BaseTest {
    
    @Mock
    private MoodRecordMapper moodRecordMapper;
    
    @InjectMocks
    private MoodRecordServiceImpl moodRecordService;
    
    private MoodRecordRequest moodRecordRequest;
    private MoodRecord testMoodRecord;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 准备情绪记录请求数据
        moodRecordRequest = new MoodRecordRequest();
        moodRecordRequest.setRecordDate(LocalDate.now());
        moodRecordRequest.setRecordTime(LocalDateTime.now());
        moodRecordRequest.setMoodType(1); // 开心
        moodRecordRequest.setMoodScore(8);
        moodRecordRequest.setTriggerEvent("完成了重要任务");
        
        // 准备测试情绪记录
        testMoodRecord = new MoodRecord();
        testMoodRecord.setId(1L);
        testMoodRecord.setStudentId(TEST_STUDENT_ID);
        testMoodRecord.setRecordDate(LocalDate.now());
        testMoodRecord.setRecordTime(LocalDateTime.now());
        testMoodRecord.setMoodType(1);
        testMoodRecord.setMoodScore(8);
        testMoodRecord.setTriggerEvent("完成了重要任务");
    }
    
    @Test
    @DisplayName("添加情绪记录 - 成功")
    public void testAddRecord_Success() {
        // Given
        when(moodRecordMapper.insert(any(MoodRecord.class))).thenReturn(1);
        
        // When
        Result result = moodRecordService.addRecord(TEST_STUDENT_ID, moodRecordRequest);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("成功"));
        verify(moodRecordMapper, times(1)).insert(any(MoodRecord.class));
    }
    
    @Test
    @DisplayName("添加情绪记录 - 必填项缺失")
    public void testAddRecord_MissingRequiredFields() {
        // Given
        moodRecordRequest.setMoodType(null);
        
        // When
        Result result = moodRecordService.addRecord(TEST_STUDENT_ID, moodRecordRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        verify(moodRecordMapper, never()).insert(any(MoodRecord.class));
    }
    
    @Test
    @DisplayName("更新情绪记录 - 成功")
    public void testUpdateRecord_Success() {
        // Given
        when(moodRecordMapper.selectById(anyLong())).thenReturn(testMoodRecord);
        when(moodRecordMapper.updateById(any(MoodRecord.class))).thenReturn(1);
        
        // When
        Result result = moodRecordService.updateRecord(1L, TEST_STUDENT_ID, moodRecordRequest);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("更新成功"));
        verify(moodRecordMapper, times(1)).selectById(anyLong());
        verify(moodRecordMapper, times(1)).updateById(any(MoodRecord.class));
    }
    
    @Test
    @DisplayName("更新情绪记录 - 记录不存在")
    public void testUpdateRecord_NotExists() {
        // Given
        when(moodRecordMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = moodRecordService.updateRecord(1L, TEST_STUDENT_ID, moodRecordRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("不存在"));
        verify(moodRecordMapper, times(1)).selectById(anyLong());
        verify(moodRecordMapper, never()).updateById(any(MoodRecord.class));
    }
    
    @Test
    @DisplayName("更新情绪记录 - 无权限")
    public void testUpdateRecord_NoPermission() {
        // Given
        testMoodRecord.setStudentId(999L);
        when(moodRecordMapper.selectById(anyLong())).thenReturn(testMoodRecord);
        
        // When
        Result result = moodRecordService.updateRecord(1L, TEST_STUDENT_ID, moodRecordRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("权限") || result.getMessage().contains("无权"));
        verify(moodRecordMapper, times(1)).selectById(anyLong());
        verify(moodRecordMapper, never()).updateById(any(MoodRecord.class));
    }
    
    @Test
    @DisplayName("删除情绪记录 - 成功")
    public void testDeleteRecord_Success() {
        // Given
        when(moodRecordMapper.selectById(anyLong())).thenReturn(testMoodRecord);
        when(moodRecordMapper.deleteById(anyLong())).thenReturn(1);
        
        // When
        Result result = moodRecordService.deleteRecord(1L, TEST_STUDENT_ID);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("删除成功"));
        verify(moodRecordMapper, times(1)).selectById(anyLong());
        verify(moodRecordMapper, times(1)).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("删除情绪记录 - 记录不存在")
    public void testDeleteRecord_NotExists() {
        // Given
        when(moodRecordMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = moodRecordService.deleteRecord(1L, TEST_STUDENT_ID);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("不存在"));
        verify(moodRecordMapper, times(1)).selectById(anyLong());
        verify(moodRecordMapper, never()).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("查询情绪记录列表 - 成功")
    public void testListRecords_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<MoodRecord> records = Arrays.asList(testMoodRecord);
        
        Page<MoodRecord> page = new Page<>(1, 10);
        page.setRecords(records);
        page.setTotal(1);
        
        when(moodRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        
        // When
        Result result = moodRecordService.listRecords(TEST_STUDENT_ID, startDate, endDate, 1, 10);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(moodRecordMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("查询情绪记录列表 - 空结果")
    public void testListRecords_EmptyResult() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        Page<MoodRecord> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        page.setTotal(0);
        
        when(moodRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        
        // When
        Result result = moodRecordService.listRecords(TEST_STUDENT_ID, startDate, endDate, 1, 10);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(moodRecordMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }
}
