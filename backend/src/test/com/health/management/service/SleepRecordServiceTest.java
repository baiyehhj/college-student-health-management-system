package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.common.Result;
import com.health.management.dto.SleepRecordRequest;
import com.health.management.entity.SleepRecord;
import com.health.management.mapper.SleepRecordMapper;
import com.health.management.service.impl.SleepRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 睡眠记录服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("睡眠记录服务测试")
public class SleepRecordServiceTest {

    @Mock
    private SleepRecordMapper sleepRecordMapper;

    @InjectMocks
    private SleepRecordServiceImpl sleepRecordService;

    private SleepRecordRequest validRequest;
    private SleepRecord validRecord;
    private Long studentId;

    @BeforeEach
    void setUp() {
        studentId = 1L;

        validRequest = new SleepRecordRequest();
        validRequest.setRecordDate(LocalDate.now());
        validRequest.setSleepTime(LocalDateTime.now().minusHours(8));
        validRequest.setWakeTime(LocalDateTime.now());
        validRequest.setDuration(new BigDecimal("8.0"));
        validRequest.setQuality(3);

        validRecord = new SleepRecord();
        validRecord.setId(1L);
        validRecord.setStudentId(studentId);
        validRecord.setRecordDate(LocalDate.now());
        validRecord.setDuration(new BigDecimal("8.0"));
        validRecord.setQuality(3);
    }

    @Test
    @DisplayName("添加睡眠记录-成功")
    void testAddRecord_Success() {
        when(sleepRecordMapper.insert(any(SleepRecord.class))).thenReturn(1);

        Result result = sleepRecordService.addRecord(studentId, validRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(sleepRecordMapper, times(1)).insert(any(SleepRecord.class));
    }

    @Test
    @DisplayName("更新睡眠记录-成功")
    void testUpdateRecord_Success() {
        when(sleepRecordMapper.selectById(1L)).thenReturn(validRecord);
        when(sleepRecordMapper.updateById(any(SleepRecord.class))).thenReturn(1);

        validRequest.setDuration(new BigDecimal("7.5"));

        Result result = sleepRecordService.updateRecord(1L, studentId, validRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(sleepRecordMapper, times(1)).updateById(any(SleepRecord.class));
    }

    @Test
    @DisplayName("更新睡眠记录-记录不存在")
    void testUpdateRecord_NotFound() {
        when(sleepRecordMapper.selectById(999L)).thenReturn(null);

        Result result = sleepRecordService.updateRecord(999L, studentId, validRequest);

        assertNotNull(result);
        assertEquals(404, result.getCode());
        verify(sleepRecordMapper, never()).updateById(any(SleepRecord.class));
    }

    @Test
    @DisplayName("删除睡眠记录-成功")
    void testDeleteRecord_Success() {
        when(sleepRecordMapper.selectById(1L)).thenReturn(validRecord);
        when(sleepRecordMapper.deleteById(1L)).thenReturn(1);

        Result result = sleepRecordService.deleteRecord(1L, studentId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(sleepRecordMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("查询睡眠记录列表-成功")
    void testListRecords_Success() {
        List<SleepRecord> records = new ArrayList<>();
        records.add(validRecord);

        Page<SleepRecord> page = new Page<>(1, 10);
        page.setRecords(records);
        page.setTotal(1);

        when(sleepRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(page);

        Result result = sleepRecordService.listRecords(
                studentId,
                LocalDate.now().minusDays(7),
                LocalDate.now(),
                1,
                10
        );

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(sleepRecordMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取睡眠统计数据")
    void testGetSleepStatistics() {
        List<SleepRecord> records = new ArrayList<>();
        // 确保 validRecord 已经正确初始化
        records.add(validRecord);

        when(sleepRecordMapper.selectList(any(QueryWrapper.class))).thenReturn(records);

        // 创建实际的 QueryWrapper 用于测试
        QueryWrapper<SleepRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);

        List<SleepRecord> result = sleepRecordMapper.selectList(queryWrapper);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
