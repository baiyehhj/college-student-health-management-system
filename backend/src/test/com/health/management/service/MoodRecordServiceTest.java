package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.common.Result;
import com.health.management.dto.MoodRecordRequest;
import com.health.management.entity.MoodRecord;
import com.health.management.mapper.MoodRecordMapper;
import com.health.management.service.impl.MoodRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 心情记录服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("心情记录服务测试")
public class MoodRecordServiceTest {

    @Mock
    private MoodRecordMapper moodRecordMapper;

    @InjectMocks
    private MoodRecordServiceImpl moodRecordService;

    private MoodRecordRequest validRequest;
    private MoodRecord validRecord;
    private Long studentId;

    @BeforeEach
    void setUp() {
        studentId = 1L;

        validRequest = new MoodRecordRequest();
        validRequest.setRecordDate(LocalDate.now());
        validRequest.setMoodType(1);
        validRequest.setMoodScore(4);
        validRequest.setDescription("今天心情不错");

        validRecord = new MoodRecord();
        validRecord.setId(1L);
        validRecord.setStudentId(studentId);
        validRecord.setRecordDate(LocalDate.now());
        validRecord.setMoodType(1);
        validRecord.setMoodScore(4);
    }

    @Test
    @DisplayName("添加心情记录-成功")
    void testAddRecord_Success() {
        when(moodRecordMapper.insert(any(MoodRecord.class))).thenReturn(1);

        Result result = moodRecordService.addRecord(studentId, validRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(moodRecordMapper, times(1)).insert(any(MoodRecord.class));
    }

    @Test
    @DisplayName("更新心情记录-成功")
    void testUpdateRecord_Success() {
        when(moodRecordMapper.selectById(1L)).thenReturn(validRecord);
        when(moodRecordMapper.updateById(any(MoodRecord.class))).thenReturn(1);

        validRequest.setMoodScore(5);

        Result result = moodRecordService.updateRecord(1L, studentId, validRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(moodRecordMapper, times(1)).updateById(any(MoodRecord.class));
    }

    @Test
    @DisplayName("更新心情记录-记录不存在")
    void testUpdateRecord_NotFound() {
        when(moodRecordMapper.selectById(999L)).thenReturn(null);

        Result result = moodRecordService.updateRecord(999L, studentId, validRequest);

        assertNotNull(result);
        assertEquals(404, result.getCode());
        verify(moodRecordMapper, never()).updateById(any(MoodRecord.class));
    }

    @Test
    @DisplayName("删除心情记录-成功")
    void testDeleteRecord_Success() {
        when(moodRecordMapper.selectById(1L)).thenReturn(validRecord);
        when(moodRecordMapper.deleteById(1L)).thenReturn(1);

        Result result = moodRecordService.deleteRecord(1L, studentId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(moodRecordMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("查询心情记录列表-成功")
    void testListRecords_Success() {
        List<MoodRecord> records = new ArrayList<>();
        records.add(validRecord);

        Page<MoodRecord> page = new Page<>(1, 10);
        page.setRecords(records);
        page.setTotal(1);

        when(moodRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(page);

        Result result = moodRecordService.listRecords(
                studentId,
                LocalDate.now().minusDays(7),
                LocalDate.now(),
                1,
                10
        );

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(moodRecordMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    @DisplayName("查询心情统计数据")
    void testGetMoodStatistics() {
        List<MoodRecord> records = new ArrayList<>();
        records.add(validRecord);

        when(moodRecordMapper.selectList(any(QueryWrapper.class))).thenReturn(records);


        List<MoodRecord> result = moodRecordMapper.selectList(any(QueryWrapper.class));

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
