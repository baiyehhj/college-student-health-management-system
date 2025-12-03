package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.common.Result;
import com.health.management.dto.DietRecordRequest;
import com.health.management.entity.DietRecord;
import com.health.management.mapper.DietRecordMapper;
import com.health.management.service.impl.DietRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 饮食记录服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("饮食记录服务测试")
public class DietRecordServiceTest {

    @Mock
    private DietRecordMapper dietRecordMapper;

    @InjectMocks
    private DietRecordServiceImpl dietRecordService;

    private DietRecordRequest validRequest;
    private DietRecord validRecord;
    private Long studentId;

    @BeforeEach
    void setUp() {
        studentId = 1L;

        validRequest = new DietRecordRequest();
        validRequest.setRecordDate(LocalDate.now());
        validRequest.setMealType(1);
        validRequest.setFoodName("鸡蛋");
        validRequest.setFoodCategory("蛋类");
        validRequest.setCalories(new BigDecimal("70"));
        validRequest.setProtein(new BigDecimal("6"));
        validRequest.setCarbs(new BigDecimal("1"));
        validRequest.setFat(new BigDecimal("5"));

        validRecord = new DietRecord();
        validRecord.setId(1L);
        validRecord.setStudentId(studentId);
        validRecord.setRecordDate(LocalDate.now());
        validRecord.setMealType(1);
        validRecord.setFoodName("鸡蛋");
        validRecord.setCalories(new BigDecimal("70"));
    }

    @Test
    @DisplayName("添加饮食记录-成功")
    void testAddRecord_Success() {
        when(dietRecordMapper.insert(any(DietRecord.class))).thenReturn(1);

        Result result = dietRecordService.addRecord(studentId, validRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(dietRecordMapper, times(1)).insert(any(DietRecord.class));
    }

    @Test
    @DisplayName("更新饮食记录-成功")
    void testUpdateRecord_Success() {
        when(dietRecordMapper.selectById(1L)).thenReturn(validRecord);
        when(dietRecordMapper.updateById(any(DietRecord.class))).thenReturn(1);

        validRequest.setFoodName("煎蛋");

        Result result = dietRecordService.updateRecord(1L, studentId, validRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(dietRecordMapper, times(1)).updateById(any(DietRecord.class));
    }

    @Test
    @DisplayName("更新饮食记录-记录不存在")
    void testUpdateRecord_NotFound() {
        when(dietRecordMapper.selectById(999L)).thenReturn(null);

        Result result = dietRecordService.updateRecord(999L, studentId, validRequest);

        assertNotNull(result);
        assertEquals(404, result.getCode());
        verify(dietRecordMapper, never()).updateById(any(DietRecord.class));
    }

    @Test
    @DisplayName("删除饮食记录-成功")
    void testDeleteRecord_Success() {
        when(dietRecordMapper.selectById(1L)).thenReturn(validRecord);
        when(dietRecordMapper.deleteById(1L)).thenReturn(1);

        Result result = dietRecordService.deleteRecord(1L, studentId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(dietRecordMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("查询饮食记录列表-成功")
    void testListRecords_Success() {
        List<DietRecord> records = new ArrayList<>();
        records.add(validRecord);

        Page<DietRecord> page = new Page<>(1, 10);
        page.setRecords(records);
        page.setTotal(1);

        when(dietRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(page);

        Result result = dietRecordService.listRecords(
                studentId,
                LocalDate.now().minusDays(7),
                LocalDate.now(),
                1,
                10
        );

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(dietRecordMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取每日饮食统计-成功")
    void testGetDailyStats_Success() {
        List<DietRecord> records = new ArrayList<>();
        records.add(validRecord);

        when(dietRecordMapper.selectList(any(QueryWrapper.class))).thenReturn(records);

        Result result = dietRecordService.getDailyStats(studentId, LocalDate.now());

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(dietRecordMapper, times(1)).selectList(any(QueryWrapper.class));
    }
}
