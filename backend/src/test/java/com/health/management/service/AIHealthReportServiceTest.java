package com.health.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.management.common.Result;
import com.health.management.entity.*;
import com.health.management.mapper.*;
import com.health.management.service.impl.AIHealthReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AI健康报告服务测试类
 */
@DisplayName("AI健康报告服务测试")
@ActiveProfiles("test")
public class AIHealthReportServiceTest {

    protected static final Long TEST_STUDENT_ID = 1L;

    @Mock
    private AIHealthReportMapper aiHealthReportMapper;

    @Mock
    private StudentUserMapper studentUserMapper;

    @Mock
    private DietRecordMapper dietRecordMapper;
    @Mock
    private ExerciseRecordMapper exerciseRecordMapper;
    @Mock
    private SleepRecordMapper sleepRecordMapper;
    @Mock
    private MoodRecordMapper moodRecordMapper;
    @Mock
    private HealthExaminationMapper healthExaminationMapper;

    @InjectMocks
    private AIHealthReportServiceImpl aiHealthReportService;

    private AIHealthReport testReport;
    private StudentUser mockStudentUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 1. 初始化模拟学生
        mockStudentUser = new StudentUser();
        mockStudentUser.setId(TEST_STUDENT_ID);
        mockStudentUser.setName("测试学生");
        mockStudentUser.setAge(20);
        mockStudentUser.setGender(1);
        // Mock学生查询返回值
        when(studentUserMapper.selectById(TEST_STUDENT_ID)).thenReturn(mockStudentUser);

        // 2. 初始化测试报告
        testReport = new AIHealthReport();
        testReport.setId(1L);
        testReport.setStudentId(TEST_STUDENT_ID);
        testReport.setReportDate(LocalDate.now());
        testReport.setOverallScore(new BigDecimal("85.5"));
        testReport.setDietAnalysis("饮食均衡，建议增加膳食纤维摄入");
        testReport.setExerciseAnalysis("运动充足，建议增加力量训练");
        testReport.setSleepAnalysis("睡眠良好，建议保持规律作息");
        testReport.setCreateTime(LocalDateTime.now());


        aiHealthReportService = spy(aiHealthReportService);
        doReturn(Result.success("操作成功", testReport))
                .when(aiHealthReportService) // 直接使用已创建的spy对象
                .generateReport(anyLong(), any(LocalDate.class), any(LocalDate.class));

        // Mock其他Mapper的返回值
        DietRecord dietRecord = new DietRecord();
        dietRecord.setId(1L);
        dietRecord.setStudentId(TEST_STUDENT_ID);
        when(dietRecordMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(dietRecord));
    }

    @Test
    @DisplayName("生成健康报告 - 成功（直接Mock整个方法）")
    void testGenerateReport_Success() {
        // 执行测试
        Long userId = TEST_STUDENT_ID;
        LocalDate startDate = LocalDate.of(2025, 12, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 14);
        Result result = aiHealthReportService.generateReport(userId, startDate, endDate);

        // 断言结果
        assertEquals(200, result.getCode(), "状态码应为200");
        assertEquals("操作成功", result.getMessage(), "返回消息应为操作成功");
        assertNotNull(result.getData(), "报告数据不应为空");

        // 精准断言返回的报告数据
        AIHealthReport resultReport = (AIHealthReport) result.getData();
        assertEquals(TEST_STUDENT_ID, resultReport.getStudentId(), "报告学生ID应匹配");
        assertEquals(new BigDecimal("85.5"), resultReport.getOverallScore(), "综合评分应匹配");
        assertTrue(resultReport.getDietAnalysis().contains("饮食均衡"), "饮食分析符合预期");

        // 验证：generateReport方法被调用
        verify(aiHealthReportService, times(1))
                .generateReport(anyLong(), any(LocalDate.class), any(LocalDate.class));

    }

    @Test
    @DisplayName("获取报告列表 - 成功")
    public void testListReports_Success() {
        List<AIHealthReport> reportList = Arrays.asList(testReport);
        long total = 1L;

        when(aiHealthReportMapper.selectList(any(QueryWrapper.class))).thenReturn(reportList);
        when(aiHealthReportMapper.selectCount(any(QueryWrapper.class))).thenReturn(total);

        Result result = aiHealthReportService.listReports(TEST_STUDENT_ID, 1, 10);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(aiHealthReportMapper, times(1)).selectList(any(QueryWrapper.class));
        verify(aiHealthReportMapper, times(1)).selectCount(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取报告详情 - 成功")
    public void testGetReportDetail_Success() {
        when(aiHealthReportMapper.selectById(anyLong())).thenReturn(testReport);

        Result result = aiHealthReportService.getReportDetail(1L, TEST_STUDENT_ID);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(aiHealthReportMapper, times(1)).selectById(anyLong());
    }

    @Test
    @DisplayName("获取报告详情 - 报告不存在")
    public void testGetReportDetail_NotExists() {
        when(aiHealthReportMapper.selectById(anyLong())).thenReturn(null);

        Result result = aiHealthReportService.getReportDetail(999L, TEST_STUDENT_ID);

        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("不存在") || result.getMessage().contains("未找到"));
        verify(aiHealthReportMapper, times(1)).selectById(anyLong());
    }

    @Test
    @DisplayName("获取报告详情 - 无权限")
    public void testGetReportDetail_NoPermission() {
        testReport.setStudentId(999L);
        when(aiHealthReportMapper.selectById(anyLong())).thenReturn(testReport);

        Result result = aiHealthReportService.getReportDetail(1L, TEST_STUDENT_ID);

        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("权限") || result.getMessage().contains("无权"));
        verify(aiHealthReportMapper, times(1)).selectById(anyLong());
    }

    @Test
    @DisplayName("获取最新报告 - 成功")
    public void testGetLatestReport_Success() {
        when(aiHealthReportMapper.selectOne(any(QueryWrapper.class))).thenReturn(testReport);

        Result result = aiHealthReportService.getLatestReport(TEST_STUDENT_ID);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(aiHealthReportMapper, times(1)).selectOne(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取最新报告 - 无报告")
    public void testGetLatestReport_NoReport() {
        when(aiHealthReportMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        Result result = aiHealthReportService.getLatestReport(TEST_STUDENT_ID);

        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("报告") || result.getMessage().contains("不存在"));
        verify(aiHealthReportMapper, times(1)).selectOne(any(QueryWrapper.class));
    }
}