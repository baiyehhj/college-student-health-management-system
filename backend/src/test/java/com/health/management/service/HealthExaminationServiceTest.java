package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.management.BaseTest;
import com.health.management.common.Result;
import com.health.management.dto.HealthExaminationRequest;
import com.health.management.entity.HealthExamination;
import com.health.management.mapper.HealthExaminationMapper;
import com.health.management.service.impl.HealthExaminationServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 健康体检服务测试类
 */
@DisplayName("健康体检服务测试")
public class HealthExaminationServiceTest extends BaseTest {
    
    @Mock
    private HealthExaminationMapper healthExaminationMapper;
    
    @InjectMocks
    private HealthExaminationServiceImpl healthExaminationService;
    
    private HealthExaminationRequest examinationRequest;
    private HealthExamination testExamination;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 准备体检请求数据
        examinationRequest = new HealthExaminationRequest();
        examinationRequest.setExamDate(LocalDate.now());
        examinationRequest.setExamType("常规体检");
        examinationRequest.setHeight(new BigDecimal("170.0"));
        examinationRequest.setWeight(new BigDecimal("65.5"));
        examinationRequest.setBloodPressureHigh(120);
        examinationRequest.setBloodPressureLow(80);
        examinationRequest.setHeartRate(75);
        examinationRequest.setVisionLeft(new BigDecimal("5.0"));
        examinationRequest.setVisionRight(new BigDecimal("5.0"));
        examinationRequest.setOverallConclusion("体检正常");
        examinationRequest.setDoctorAdvice("保持良好生活习惯");
        
        // 准备测试体检记录
        testExamination = new HealthExamination();
        testExamination.setId(1L);
        testExamination.setStudentId(TEST_STUDENT_ID);
        testExamination.setExamDate(LocalDate.now());
        testExamination.setHeight(new BigDecimal("170.0"));
        testExamination.setWeight(new BigDecimal("65.5"));
        testExamination.setBmi(new BigDecimal("22.6"));
        testExamination.setHeartRate(75);
    }
    
    @Test
    @DisplayName("添加体检记录 - 成功")
    public void testAddExamination_Success() {
        // Given
        when(healthExaminationMapper.insert(any(HealthExamination.class))).thenReturn(1);
        
        // When
        Result result = healthExaminationService.addExamination(TEST_STUDENT_ID, examinationRequest);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("成功"));
        verify(healthExaminationMapper, times(1)).insert(any(HealthExamination.class));
    }
    
    @Test
    @DisplayName("添加体检记录 - 必填项缺失")
    public void testAddExamination_MissingRequiredFields() {
        // Given
        examinationRequest.setExamDate(null);
        
        // When & Then
        try {
            healthExaminationService.addExamination(TEST_STUDENT_ID, examinationRequest);
        } catch (Exception e) {
            // 预期抛出异常
            assertNotNull(e);
        }
        
        verify(healthExaminationMapper, never()).insert(any(HealthExamination.class));
    }
    
    @Test
    @DisplayName("更新体检记录 - 成功")
    public void testUpdateExamination_Success() {
        // Given
        when(healthExaminationMapper.selectById(anyLong())).thenReturn(testExamination);
        when(healthExaminationMapper.updateById(any(HealthExamination.class))).thenReturn(1);
        
        // When
        Result result = healthExaminationService.updateExamination(1L, TEST_STUDENT_ID, examinationRequest);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("更新成功") || result.getMessage().contains("成功"));
        verify(healthExaminationMapper, times(1)).selectById(anyLong());
        verify(healthExaminationMapper, times(1)).updateById(any(HealthExamination.class));
    }
    
    @Test
    @DisplayName("更新体检记录 - 记录不存在")
    public void testUpdateExamination_NotExists() {
        // Given
        when(healthExaminationMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = healthExaminationService.updateExamination(1L, TEST_STUDENT_ID, examinationRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("不存在") || result.getMessage().contains("未找到"));
        verify(healthExaminationMapper, times(1)).selectById(anyLong());
        verify(healthExaminationMapper, never()).updateById(any(HealthExamination.class));
    }
    
    @Test
    @DisplayName("更新体检记录 - 无权限")
    public void testUpdateExamination_NoPermission() {
        // Given
        testExamination.setStudentId(999L);
        when(healthExaminationMapper.selectById(anyLong())).thenReturn(testExamination);
        
        // When
        Result result = healthExaminationService.updateExamination(1L, TEST_STUDENT_ID, examinationRequest);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("权限") || result.getMessage().contains("无权"));
        verify(healthExaminationMapper, times(1)).selectById(anyLong());
        verify(healthExaminationMapper, never()).updateById(any(HealthExamination.class));
    }
    
    @Test
    @DisplayName("删除体检记录 - 成功")
    public void testDeleteExamination_Success() {
        // Given
        when(healthExaminationMapper.selectById(anyLong())).thenReturn(testExamination);
        when(healthExaminationMapper.deleteById(anyLong())).thenReturn(1);
        
        // When
        Result result = healthExaminationService.deleteExamination(1L, TEST_STUDENT_ID);
        
        // Then
        assertEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("删除成功") || result.getMessage().contains("成功"));
        verify(healthExaminationMapper, times(1)).selectById(anyLong());
        verify(healthExaminationMapper, times(1)).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("删除体检记录 - 记录不存在")
    public void testDeleteExamination_NotExists() {
        // Given
        when(healthExaminationMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = healthExaminationService.deleteExamination(1L, TEST_STUDENT_ID);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("不存在"));
        verify(healthExaminationMapper, times(1)).selectById(anyLong());
        verify(healthExaminationMapper, never()).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("查询体检记录列表 - 成功")
    public void testListExaminations_Success() {
        // Given
        Page<HealthExamination> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testExamination));
        page.setTotal(1);
        
        when(healthExaminationMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        
        // When
        Result result = healthExaminationService.listExaminations(TEST_STUDENT_ID, 1, 10);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(healthExaminationMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("查询体检记录列表 - 空结果")
    public void testListExaminations_EmptyResult() {
        // Given
        Page<HealthExamination> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        page.setTotal(0);
        
        when(healthExaminationMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);
        
        // When
        Result result = healthExaminationService.listExaminations(TEST_STUDENT_ID, 1, 10);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(healthExaminationMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取最新体检记录 - 成功")
    public void testGetLatestExamination_Success() {
        // Given
        when(healthExaminationMapper.selectOne(any(QueryWrapper.class))).thenReturn(testExamination);
        
        // When
        Result result = healthExaminationService.getLatestExamination(TEST_STUDENT_ID);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(healthExaminationMapper, times(1)).selectOne(any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取最新体检记录 - 无记录")
    public void testGetLatestExamination_NoRecord() {
        // Given
        when(healthExaminationMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        
        // When
        Result result = healthExaminationService.getLatestExamination(TEST_STUDENT_ID);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("记录") || result.getMessage().contains("不存在"));
        verify(healthExaminationMapper, times(1)).selectOne(any(QueryWrapper.class));
    }
    
    @Test
    @DisplayName("获取体检详情 - 成功")
    public void testGetExaminationDetail_Success() {
        // Given
        when(healthExaminationMapper.selectById(anyLong())).thenReturn(testExamination);
        
        // When
        Result result = healthExaminationService.getExaminationDetail(1L, TEST_STUDENT_ID);
        
        // Then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(healthExaminationMapper, times(1)).selectById(anyLong());
    }
    
    @Test
    @DisplayName("获取体检详情 - 记录不存在")
    public void testGetExaminationDetail_NotExists() {
        // Given
        when(healthExaminationMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Result result = healthExaminationService.getExaminationDetail(1L, TEST_STUDENT_ID);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("不存在"));
        verify(healthExaminationMapper, times(1)).selectById(anyLong());
    }
    
    @Test
    @DisplayName("获取体检详情 - 无权限")
    public void testGetExaminationDetail_NoPermission() {
        // Given
        testExamination.setStudentId(999L);
        when(healthExaminationMapper.selectById(anyLong())).thenReturn(testExamination);
        
        // When
        Result result = healthExaminationService.getExaminationDetail(1L, TEST_STUDENT_ID);
        
        // Then
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("权限") || result.getMessage().contains("无权"));
        verify(healthExaminationMapper, times(1)).selectById(anyLong());
    }
}
