package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.common.Result;
import com.health.management.dto.ExerciseRecordRequest;
import com.health.management.entity.ExerciseRecord;
import com.health.management.mapper.ExerciseRecordMapper;
import com.health.management.service.impl.ExerciseRecordServiceImpl;
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
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 运动记录服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("运动记录服务测试")
public class ExerciseRecordServiceTest {

    @Mock
    private ExerciseRecordMapper exerciseRecordMapper;

    @InjectMocks
    private ExerciseRecordServiceImpl exerciseRecordService;

    private ExerciseRecordRequest validRequest;
    private ExerciseRecord validRecord;
    private Long studentId;

    @BeforeEach
    void setUp() {
        studentId = 1L;

        validRequest = new ExerciseRecordRequest();
        validRequest.setRecordDate(LocalDate.now());
        validRequest.setExerciseType("跑步");
        validRequest.setDuration(30);
        validRequest.setCaloriesBurned(new BigDecimal("250"));
        validRequest.setIntensity(2);
        validRequest.setDistance(new BigDecimal("5.0"));

        validRecord = new ExerciseRecord();
        validRecord.setId(1L);
        validRecord.setStudentId(studentId);
        validRecord.setRecordDate(LocalDate.now());
        validRecord.setExerciseType("跑步");
        validRecord.setDuration(30);
        validRecord.setCaloriesBurned(new BigDecimal("250"));
    }

    @Test
    @DisplayName("添加运动记录-成功")
    void testAddRecord_Success() {
        when(exerciseRecordMapper.insert(any(ExerciseRecord.class))).thenReturn(1);

        Result result = exerciseRecordService.addRecord(studentId, validRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(exerciseRecordMapper, times(1)).insert(any(ExerciseRecord.class));
    }

    @Test
    @DisplayName("更新运动记录-成功")
    void testUpdateRecord_Success() {
        when(exerciseRecordMapper.selectById(1L)).thenReturn(validRecord);
        when(exerciseRecordMapper.updateById(any(ExerciseRecord.class))).thenReturn(1);

        validRequest.setDuration(45);

        Result result = exerciseRecordService.updateRecord(1L, studentId, validRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(exerciseRecordMapper, times(1)).updateById(any(ExerciseRecord.class));
    }

    @Test
    @DisplayName("更新运动记录-记录不存在")
    void testUpdateRecord_NotFound() {
        when(exerciseRecordMapper.selectById(999L)).thenReturn(null);

        Result result = exerciseRecordService.updateRecord(999L, studentId, validRequest);

        assertNotNull(result);
        assertEquals(404, result.getCode());
        verify(exerciseRecordMapper, never()).updateById(any(ExerciseRecord.class));
    }

    @Test
    @DisplayName("删除运动记录-成功")
    void testDeleteRecord_Success() {
        when(exerciseRecordMapper.selectById(1L)).thenReturn(validRecord);
        when(exerciseRecordMapper.deleteById(1L)).thenReturn(1);

        Result result = exerciseRecordService.deleteRecord(1L, studentId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(exerciseRecordMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("查询运动记录列表-成功")
    void testListRecords_Success() {
        List<ExerciseRecord> records = new ArrayList<>();
        records.add(validRecord);

        Page<ExerciseRecord> page = new Page<>(1, 10);
        page.setRecords(records);
        page.setTotal(1);

        when(exerciseRecordMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(page);

        Result result = exerciseRecordService.listRecords(
                studentId,
                LocalDate.now().minusDays(7),
                LocalDate.now(),
                1,
                10
        );

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(exerciseRecordMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    @DisplayName("按日期查询运动记录")
    void testListRecordsByDate() {
        LocalDate exerciseDate = LocalDate.of(2025, 12, 3);
        // 构造预期的运动记录
        ExerciseRecord validRecord = new ExerciseRecord();
        validRecord.setRecordDate(exerciseDate);
        List<ExerciseRecord> expectedRecords = new ArrayList<>();
        expectedRecords.add(validRecord);

        when(exerciseRecordMapper.selectList(argThat(queryWrapper -> {
            // 校验QueryWrapper中的查询条件是否符合预期
            String sqlSegment = queryWrapper.getCustomSqlSegment();
            return sqlSegment.contains("student_id = " + studentId)
                    && sqlSegment.contains("exercise_date = '" + exerciseDate + "'");
        }))).thenReturn(expectedRecords);

        // 3. 构造真实的QueryWrapper
        QueryWrapper<ExerciseRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId)
                .eq("exercise_date", exerciseDate);

        // 4. 执行测试
        List<ExerciseRecord> result = exerciseRecordMapper.selectList(queryWrapper);

        // 5. 断言验证
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(studentId, result.get(0).getStudentId());
        assertEquals(exerciseDate, result.get(0).getRecordDate());

        // 6. 验证Mock被调用
        verify(exerciseRecordMapper, times(1)).selectList(queryWrapper);
    }
}
