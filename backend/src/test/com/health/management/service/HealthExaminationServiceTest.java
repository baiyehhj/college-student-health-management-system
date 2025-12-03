package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.management.common.Result;
import com.health.management.dto.HealthExaminationRequest;
import com.health.management.entity.HealthExamination;
import com.health.management.mapper.HealthExaminationMapper;
import com.health.management.service.impl.HealthExaminationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 健康体检服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("健康体检服务测试")
@SpringBootTest
public class HealthExaminationServiceTest {

    @Mock
    private HealthExaminationMapper healthExaminationMapper;

    @InjectMocks
    private HealthExaminationServiceImpl healthExaminationServiceImpl;

    private HealthExaminationRequest validRequest;
    private HealthExamination validRecord;
    private Long studentId;

    @BeforeEach
    void setUp() {
        studentId = 1L;

        validRequest = new HealthExaminationRequest();
        validRequest.setExamDate(LocalDate.now());
        validRequest.setHeight(new BigDecimal("175.0"));
        validRequest.setWeight(new BigDecimal("70.0"));
        validRequest.setBloodPressureHigh(120);
        validRequest.setBloodPressureLow(80);
        validRequest.setHeartRate(75);

        validRecord = new HealthExamination();
        validRecord.setId(1L);
        validRecord.setStudentId(studentId);
        validRecord.setExamDate(LocalDate.now());
        validRecord.setHeight(new BigDecimal("175.0"));
        validRecord.setWeight(new BigDecimal("70.0"));
        validRecord.setBmi(new BigDecimal("22.86"));

        assertNotNull(healthExaminationMapper);
        assertNotNull(healthExaminationServiceImpl);
    }

    @Test
    @DisplayName("添加体检记录-成功")
    void testAddExamination_Success() {
        when(healthExaminationMapper.insert(any(HealthExamination.class))).thenReturn(1);

        Result result = healthExaminationServiceImpl.addExamination(studentId, validRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(healthExaminationMapper, times(1)).insert(any(HealthExamination.class));
    }

    @Test
    @DisplayName("更新体检记录-成功")
    void testUpdateExamination_Success() {
        when(healthExaminationMapper.selectById(1L)).thenReturn(validRecord);
        when(healthExaminationMapper.updateById(any(HealthExamination.class))).thenReturn(1);

        validRequest.setWeight(new BigDecimal("72.0"));

        Result result = healthExaminationServiceImpl.updateExamination(1L, studentId, validRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(healthExaminationMapper, times(1)).updateById(any(HealthExamination.class));
    }

    @Test
    @DisplayName("更新体检记录-记录不存在")
    void testUpdateExamination_NotFound() {
        when(healthExaminationMapper.selectById(999L)).thenReturn(null);

        Result result = healthExaminationServiceImpl.updateExamination(999L, studentId, validRequest);

        assertNotNull(result);
        assertEquals(404, result.getCode());
        verify(healthExaminationMapper, never()).updateById(any(HealthExamination.class));
    }

    @Test
    @DisplayName("删除体检记录-成功")
    void testDeleteExamination_Success() {
        when(healthExaminationMapper.selectById(1L)).thenReturn(validRecord);
        when(healthExaminationMapper.deleteById(1L)).thenReturn(1);

        Result result = healthExaminationServiceImpl.deleteExamination(1L, studentId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(healthExaminationMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("查询体检记录列表-成功")
    void testListExaminations_Success() {
    Result result = healthExaminationServiceImpl.listExaminations(studentId, 1, 10);

        assertNotNull(result);
        verify(healthExaminationMapper, times(1)).selectPage(any(), any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取最新体检记录-成功")
    void testGetLatestExamination_Success() {
        when(healthExaminationMapper.selectOne(any(QueryWrapper.class))).thenReturn(validRecord);

        Result result = healthExaminationServiceImpl.getLatestExamination(studentId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(healthExaminationMapper, times(1)).selectOne(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取最新体检记录-无记录")
    void testGetLatestExamination_NoRecords() {
        when(healthExaminationMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        Result result = healthExaminationServiceImpl.getLatestExamination(studentId);

        assertNotNull(result);
        assertEquals(200,result.getCode());
    }
}
