package com.health.management;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.management.common.Result;
import com.health.management.config.PasswordEncoderConfig;
import com.health.management.dto.*;
import com.health.management.entity.ExerciseRecord;
import com.health.management.entity.StudentUser;
import com.health.management.mapper.ExerciseRecordMapper;
import com.health.management.mapper.StudentUserMapper;
import com.health.management.service.*;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 系统综合集成测试
 * 测试完整的业务流程和模块间协作
 */
@SpringBootTest(classes = HealthManagementApplication.class)
@ActiveProfiles("test")
@DisplayName("系统综合集成测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Rollback(true)  // 测试完成后回滚，避免污染测试环境
public class SystemIntegrationTest {

    private static final String TEST_STUDENT_NO = "2024001";
    private static final String TEST_PASSWORD = "Test123456";
    private static final String TEST_ROLE = "STUDENT"; //

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private DietRecordService dietRecordService;

    @Autowired
    private ExerciseRecordService exerciseRecordService;

    @Autowired
    private SleepRecordService sleepRecordService;

    @Autowired
    private MoodRecordService moodRecordService;

    @Autowired
    private HealthExaminationService healthExaminationService;

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @Autowired
    private AIHealthReportService aiHealthReportService;

    @Autowired
    private StudentUserMapper studentUserMapper;

    @Autowired
    private ExerciseRecordMapper exerciseRecordMapper;

    @Autowired
    private PasswordEncoderConfig.PasswordEncoder passwordEncoder;

    private Long testStudentId;

    /**
     * 每个测试方法执行前的初始化：仅清理数据，不提前创建用户（避免注册冲突）
     */
    @BeforeEach
    public void setUp() {
        // 清理测试数据
        cleanupTestData(TEST_STUDENT_NO);
        // 初始化testStudentId为null
        testStudentId = null;
    }

    /**
     * 每个测试方法执行后的清理
     */
    @AfterEach
    public void tearDown() {
        cleanupTestData(TEST_STUDENT_NO);
    }

    /**
     * 所有测试完成后最终清理
     */
    @AfterAll
    public static void afterAll(
            @Autowired StudentUserMapper studentUserMapper,
            @Autowired ExerciseRecordMapper exerciseRecordMapper) {
        String studentNo = TEST_STUDENT_NO;
        try {
            // 查询用户ID
            StudentUser user = studentUserMapper.selectOne(
                    new QueryWrapper<StudentUser>().eq("student_no", studentNo)
            );
            if (user != null) {
                // 删除关联记录
                exerciseRecordMapper.delete(new QueryWrapper<ExerciseRecord>()
                        .eq("student_id", user.getId()));
                // 删除用户
                studentUserMapper.delete(new QueryWrapper<StudentUser>()
                        .eq("student_no", studentNo));
            }
            System.out.println("✓ 最终测试数据清理完成");
        } catch (Exception e) {
            System.err.println("最终清理测试数据时发生错误: " + e.getMessage());
        }
    }

    /**
     * 检查测试用户是否已存在
     */
    private boolean isTestUserExists(String studentNo) {
        StudentUser user = studentUserMapper.selectOne(
                new QueryWrapper<StudentUser>().eq("student_no", studentNo)
        );
        if (user != null) {
            testStudentId = user.getId();
            return true;
        }
        return false;
    }

    /**
     * 创建测试学生用户（备用方法）
     */
    private StudentUser createTestStudent(String studentNo) {
        StudentUser student = new StudentUser();
        student.setStudentNo(studentNo);
        student.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        student.setName("测试用户_" + studentNo);
        student.setGender(1);
        student.setAge(20);
        student.setPhone("13900139000");
        student.setEmail(studentNo + "@test.com");
        student.setMajor("计算机科学");
        student.setClassName("CS2024");
        student.setStatus(1);
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        return student;
    }

    /**
     * 安全清理测试数据：先查用户ID，再删关联数据，最后删用户
     */
    private void cleanupTestData(String studentNo) {
        try {
            // 1. 查询用户ID
            StudentUser user = studentUserMapper.selectOne(
                    new QueryWrapper<StudentUser>().eq("student_no", studentNo)
            );

            // 2. 删除关联记录
            if (user != null) {
                exerciseRecordMapper.delete(new QueryWrapper<ExerciseRecord>()
                        .eq("student_id", user.getId()));
            }

            // 3. 删除学生用户
            studentUserMapper.delete(new QueryWrapper<StudentUser>()
                    .eq("student_no", studentNo));

            System.out.println("✓ 测试数据清理完成");
        } catch (Exception e) {
            System.err.println("清理测试数据时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 安全获取Map中的值
     */
    private <T> T getMapValue(Map<String, Object> map, String key, Class<T> clazz) {
        if (map == null || !map.containsKey(key)) {
            return null;
        }
        Object value = map.get(key);
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }

    /**
     * 完整用户流程 - 注册到登录（修复角色字段问题）
     */
    @Test
    @Order(1)
    @DisplayName("完整用户流程 - 注册到登录")
    public void testCompleteUserFlow() {
        System.out.println(">>> 测试完整用户流程");

        // 1. 前置检查：确保用户不存在
        if (isTestUserExists(TEST_STUDENT_NO)) {
            fail("测试前用户已存在，可能是清理数据失败");
        }

        // 2. 用户注册
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setRole(TEST_ROLE); // 关键：设置角色
        registerRequest.setStudentNo(TEST_STUDENT_NO);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setName("集成测试用户");
        registerRequest.setGender(1);
        registerRequest.setPhone("13900139000");
        registerRequest.setEmail("integration@test.com");
        registerRequest.setMajor("软件工程");
        registerRequest.setClassName("软件2024-1班");

        Result registerResult = authService.register(registerRequest);
        String registerErrorMsg = registerResult.getMessage() != null ? registerResult.getMessage() : "未知错误";
        assertEquals(200, registerResult.getCode(),
                "注册失败，返回码：" + registerResult.getCode() + "，消息：" + registerErrorMsg);
        System.out.println("✓ 用户注册成功");

        // 3. 用户登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setStudentNo(TEST_STUDENT_NO);
        loginRequest.setPassword(TEST_PASSWORD);
        loginRequest.setRole(TEST_ROLE);

        Result loginResult = authService.login(loginRequest);
        String loginErrorMsg = loginResult.getMessage() != null ? loginResult.getMessage() : "未知错误";
        assertEquals(200, loginResult.getCode(),
                "登录失败，返回码：" + loginResult.getCode() + "，消息：" + loginErrorMsg);
        assertNotNull(loginResult.getData(), "登录应返回Token数据");

        // 安全解析Token
        String token = null;
        if (loginResult.getData() instanceof Map) {
            token = getMapValue((Map<String, Object>) loginResult.getData(), "token", String.class);
        } else if (loginResult.getData() instanceof String) {
            token = (String) loginResult.getData();
        }

        assertNotNull(token, "Token不应为空");
        assertTrue(token.length() > 10, "Token格式应有效");
        System.out.println("✓ 用户登录成功，Token: " + token.substring(0, 20) + "...");

        // 4. 验证用户数据
        StudentUser student = studentUserMapper.selectOne(
                new QueryWrapper<StudentUser>().eq("student_no", TEST_STUDENT_NO)
        );
        assertNotNull(student, "应查询到创建的学生记录");
        assertEquals("集成测试用户", student.getName());
        assertEquals(TEST_STUDENT_NO, student.getStudentNo());
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, student.getPassword()));

        testStudentId = student.getId();
        System.out.println("✓ 用户信息验证成功，ID: " + testStudentId);

        System.out.println("完整用户流程测试通过\n");
    }

    /**
     * 健康数据记录完整流程
     */
    @Test
    @Order(2)
    @DisplayName("健康数据记录完整流程")
    public void testHealthDataRecordingFlow() {
        System.out.println(">>> 测试健康数据记录流程");

        // 确保用户已创建（如果testStudentId为空，手动创建）
        ensureTestUserCreated();
        assertNotNull(testStudentId, "测试用户ID不应为空");

        // 1. 添加体检记录（移除exam_type字段，修复SQL错误）
        HealthExaminationRequest examRequest = new HealthExaminationRequest();
        examRequest.setHeight(new BigDecimal("175.0"));
        examRequest.setWeight(new BigDecimal("70.0"));
        examRequest.setBloodPressureHigh(120);
        examRequest.setBloodPressureLow(80);
        examRequest.setHeartRate(72);
        examRequest.setExamDate(LocalDate.now());

        Result examResult = healthExaminationService.addExamination(testStudentId, examRequest);
        assertEquals(200, examResult.getCode(), "添加体检记录失败");
        System.out.println("✓ 体检记录创建成功");

        // 2. 添加运动记录
        ExerciseRecordRequest exerciseRequest = new ExerciseRecordRequest();
        exerciseRequest.setExerciseType("跑步");
        exerciseRequest.setDuration(30);
        exerciseRequest.setCaloriesBurned(new BigDecimal("300"));
        exerciseRequest.setRecordDate(LocalDate.now());

        Result exerciseResult = exerciseRecordService.addRecord(testStudentId, exerciseRequest);
        assertEquals(200, exerciseResult.getCode(), "添加运动记录失败");
        System.out.println("✓ 运动记录创建成功");

        // 3. 添加睡眠记录
        SleepRecordRequest sleepRequest = new SleepRecordRequest();
        sleepRequest.setRecordDate(LocalDate.now());
        sleepRequest.setSleepTime(LocalDateTime.now().minusHours(8));
        sleepRequest.setWakeTime(LocalDateTime.now());
        sleepRequest.setDuration(new BigDecimal("8.0"));
        sleepRequest.setQuality(4);

        Result sleepResult = sleepRecordService.addRecord(testStudentId, sleepRequest);
        assertEquals(200, sleepResult.getCode(), "添加睡眠记录失败");
        System.out.println("✓ 睡眠记录创建成功");

        // 4. 添加心情记录（移除record_time和trigger_event字段，修复SQL错误）
        MoodRecordRequest moodRequest = new MoodRecordRequest();
        moodRequest.setMoodType(1); // 1=开心
        moodRequest.setMoodScore(5);
        moodRequest.setRecordDate(LocalDate.now());

        Result moodResult = moodRecordService.addRecord(testStudentId, moodRequest);
        assertEquals(200, moodResult.getCode(), "添加心情记录失败");
        System.out.println("✓ 心情记录创建成功");

        // 5. 添加饮食记录
        DietRecordRequest dietRequest = new DietRecordRequest();
        dietRequest.setMealType(2); // 2=午餐
        dietRequest.setFoodName("健康餐");
        dietRequest.setCalories(new BigDecimal("600"));
        dietRequest.setProtein(new BigDecimal("30.0"));
        dietRequest.setFat(new BigDecimal("20.0"));
        dietRequest.setCarbs(new BigDecimal("80.0"));
        dietRequest.setRecordDate(LocalDate.now());

        Result dietResult = dietRecordService.addRecord(testStudentId, dietRequest);
        assertEquals(200, dietResult.getCode(), "添加饮食记录失败");
        System.out.println("✓ 饮食记录创建成功");

        System.out.println("✅ 健康数据记录流程测试通过\n");
    }

    /**
     * 数据分析流程
     */
    @Test
    @Order(3)
    @DisplayName("数据分析流程")
    public void testDataAnalysisFlow() {
        System.out.println(">>> 测试数据分析流程");

        // 确保用户已创建
        ensureTestUserCreated();
        assertNotNull(testStudentId, "测试用户ID不应为空");

        // 准备测试数据
        prepareTestHealthData(testStudentId);
        System.out.println("✓ 测试数据准备完成");

        //1. 获取体重趋势
        Result weightTrend = dataAnalysisService.getWeightTrend(testStudentId, 50);
        assertEquals(200, weightTrend.getCode(), "获取体重趋势失败");
        assertNotNull(weightTrend.getData(), "体重趋势数据不应为空");
        System.out.println("✓ 体重趋势分析成功");

        // 2. 获取运动统计
        Result exerciseStats = dataAnalysisService.getExerciseStats(testStudentId, 7);
        assertEquals(200, exerciseStats.getCode(), "获取运动统计失败");
        assertNotNull(exerciseStats.getData(), "运动统计数据不应为空");
        System.out.println("✓ 运动统计分析成功");

        // 3. 获取睡眠趋势
        Result sleepTrend = dataAnalysisService.getSleepTrend(testStudentId, 7);
        assertEquals(200, sleepTrend.getCode(), "获取睡眠趋势失败");
        assertNotNull(sleepTrend.getData(), "睡眠趋势数据不应为空");
        System.out.println("✓ 睡眠趋势分析成功");

        // 4. 获取心情分布
        Result moodDistribution = dataAnalysisService.getMoodDistribution(testStudentId, 7);
        assertEquals(200, moodDistribution.getCode(), "获取心情分布失败");
        assertNotNull(moodDistribution.getData(), "心情分布数据不应为空");
        System.out.println("✓ 心情分布分析成功");

        // 5. 获取营养统计
        Result nutritionStats = dataAnalysisService.getNutritionStats(testStudentId, 7);
        assertEquals(200, nutritionStats.getCode(), "获取营养统计失败");
        assertNotNull(nutritionStats.getData(), "营养统计数据不应为空");
        System.out.println("✓ 营养统计分析成功");

        System.out.println("✅ 数据分析流程测试通过\n");
    }

    /**
     * AI健康报告生成流程
     */
    @Test
    @Order(4)
    @DisplayName("AI健康报告生成流程")
    public void testAIHealthReportFlow() {
        System.out.println(">>> 测试AI健康报告生成流程");

        // 确保用户已创建
        ensureTestUserCreated();
        assertNotNull(testStudentId, "测试用户ID不应为空");

        // 创建7天的健康数据
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);

            // 体检记录
            HealthExaminationRequest examRequest = new HealthExaminationRequest();
            examRequest.setHeight(new BigDecimal("175.0"));
            examRequest.setWeight(new BigDecimal("70.0"));
            examRequest.setBloodPressureHigh(120 + i);
            examRequest.setBloodPressureLow(80);
            examRequest.setHeartRate(72);
            examRequest.setExamDate(date);
            healthExaminationService.addExamination(testStudentId, examRequest);

            // 运动记录
            ExerciseRecordRequest exerciseRequest = new ExerciseRecordRequest();
            exerciseRequest.setExerciseType("跑步");
            exerciseRequest.setDuration(30 + i);
            exerciseRequest.setCaloriesBurned(new BigDecimal(300 + i * 10));
            exerciseRequest.setRecordDate(date);
            exerciseRecordService.addRecord(testStudentId, exerciseRequest);

            // 睡眠记录
            SleepRecordRequest sleepRequest = new SleepRecordRequest();
            sleepRequest.setRecordDate(LocalDate.now());
            sleepRequest.setSleepTime(LocalDateTime.now().minusHours(8));
            sleepRequest.setWakeTime(LocalDateTime.now());
            sleepRequest.setDuration(new BigDecimal("8.0"));
            sleepRequest.setQuality(3);
            sleepRequest.setDeepSleepDuration(new BigDecimal("2.5"));
            sleepRequest.setDreamCount(1);
            sleepRecordService.addRecord(testStudentId, sleepRequest);

            // 心情记录
            MoodRecordRequest moodRequest = new MoodRecordRequest();
            moodRequest.setMoodType(1);
            moodRequest.setMoodScore(5);
            moodRequest.setRecordDate(date);
            moodRecordService.addRecord(testStudentId, moodRequest);
        }
        System.out.println("✓ 测试数据准备完成（7天健康记录）");

        // 1. 生成健康报告
        Result generateResult = aiHealthReportService.generateReport(
                testStudentId, startDate, endDate);
        assertEquals(200, generateResult.getCode(), "生成报告失败");
        assertNotNull(generateResult.getData(), "报告数据不应为空");
        System.out.println("✓ AI健康报告生成成功");

        // 2. 获取报告列表
        Result listResult = aiHealthReportService.listReports(testStudentId, 1, 10);
        assertEquals(200, listResult.getCode(), "获取报告列表失败");

        // 验证分页数据
        if (listResult.getData() instanceof IPage) {
            IPage<?> page = (IPage<?>) listResult.getData();
            assertTrue(page.getTotal() > 0, "应有生成的报告记录");
        } else if (listResult.getData() instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) listResult.getData();
            assertTrue(((Number) Objects.requireNonNull(data.get("total"))).intValue() > 0);
        }
        System.out.println("✓ 报告列表查询成功");

        // 3. 获取最新报告
        Result latestResult = aiHealthReportService.getLatestReport(testStudentId);
        assertEquals(200, latestResult.getCode(), "获取最新报告失败");
        assertNotNull(latestResult.getData(), "最新报告数据不应为空");
        System.out.println("✓ 最新报告查询成功");

        System.out.println(" AI健康报告流程测试通过\n");
    }

    /**
     * 数据更新和删除流程
     */
    @Test
    @Order(5)
    @DisplayName("数据更新和删除流程")
    public void testDataUpdateAndDeleteFlow() {
        System.out.println(">>> 测试数据更新和删除流程");

        // 确保用户已创建
        ensureTestUserCreated();
        assertNotNull(testStudentId, "测试用户ID不应为空");

        // 创建运动记录
        ExerciseRecordRequest createRequest = new ExerciseRecordRequest();
        createRequest.setExerciseType("跑步");
        createRequest.setDuration(30);
        createRequest.setCaloriesBurned(new BigDecimal("300"));
        createRequest.setRecordDate(LocalDate.now());

        Result createResult = exerciseRecordService.addRecord(testStudentId, createRequest);
        assertEquals(200, createResult.getCode(), "创建记录失败");
        System.out.println("✓ 运动记录创建成功");

        // 获取创建的记录ID
        Long recordId = getCreatedRecordId(testStudentId);
        assertNotNull(recordId, "记录ID不应为空");

        // 1. 查询记录列表
        Result listResult = exerciseRecordService.listRecords(testStudentId, null, null, 1, 10);
        assertEquals(200, listResult.getCode(), "查询列表失败");
        System.out.println("✓ 记录列表查询成功");

        // 2. 更新记录
        ExerciseRecordRequest updateRequest = new ExerciseRecordRequest();
        updateRequest.setExerciseType("游泳");
        updateRequest.setDuration(45);
        updateRequest.setCaloriesBurned(new BigDecimal("400"));
        updateRequest.setRecordDate(LocalDate.now());

        Result updateResult = exerciseRecordService.updateRecord(recordId, testStudentId, updateRequest);
        assertEquals(200, updateResult.getCode(), "更新记录失败");
        System.out.println("✓ 记录更新成功");

        // 3. 删除记录
        Result deleteResult = exerciseRecordService.deleteRecord(recordId, testStudentId);
        assertEquals(200, deleteResult.getCode(), "删除记录失败");
        System.out.println("✓ 记录删除成功");

        // 验证删除结果
        Result verifyResult = exerciseRecordService.listRecords(testStudentId, null, null, 1, 10);
        if (verifyResult.getData() instanceof IPage) {
            IPage<?> page = (IPage<?>) verifyResult.getData();
            // 检查是否还有该记录，而不是直接断言总数为0（可能有其他测试数据）
            boolean recordFound = false;
            for (Object record : page.getRecords()) {
                try {
                    Long id = (Long) record.getClass().getMethod("getId").invoke(record);
                    if (recordId.equals(id)) {
                        recordFound = true;
                        break;
                    }
                } catch (Exception e) {
                    // 忽略反射异常
                }
            }
            assertFalse(recordFound, "记录应已被删除");
        }

        System.out.println("数据更新删除流程测试通过\n");
    }

    /**
     * 用户信息修改流程
     */
    @Test
    @Order(6)
    @DisplayName("用户信息修改流程")
    public void testUserProfileUpdateFlow() {
        System.out.println(">>> 测试用户信息修改流程");

        // 确保用户已创建
        ensureTestUserCreated();
        assertNotNull(testStudentId, "测试用户ID不应为空");

        // 1. 获取当前用户信息
        Result userInfoResult = userService.getUserInfo(testStudentId);
        assertEquals(200, userInfoResult.getCode(), "获取用户信息失败");
        assertNotNull(userInfoResult.getData(), "用户信息不应为空");
        System.out.println("✓ 用户信息查询成功");

        // 2. 更新用户信息
        UserInfoRequest updateRequest = new UserInfoRequest();
        updateRequest.setName("更新后的测试名字");
        updateRequest.setPhone("13900139001");
        updateRequest.setEmail("updated_test@test.com");
        updateRequest.setAge(21);
        updateRequest.setGender(0);

        Result updateResult = userService.updateUserInfo(testStudentId, updateRequest);
        assertEquals(200, updateResult.getCode(), "更新用户信息失败");
        System.out.println("✓ 用户信息更新成功");

        // 3. 验证更新结果
        Result verifyResult = userService.getUserInfo(testStudentId);
        assertEquals(200, verifyResult.getCode(), "验证更新失败");

        if (verifyResult.getData() instanceof Map) {
            Map<String, Object> userData = (Map<String, Object>) verifyResult.getData();
            assertEquals("更新后的测试名字", userData.get("name"));
            assertEquals("13900139001", userData.get("phone"));
            assertEquals("updated_test@test.com", userData.get("email"));
        }
        System.out.println("✓ 更新结果验证成功");

        System.out.println("用户信息修改流程测试通过\n");
    }

    /**
     * 多维度数据统计
     */
    @Test
    @Order(7)
    @DisplayName("多维度数据统计")
    public void testMultiDimensionalStatistics() {
        System.out.println(">>> 测试多维度数据统计");

        // 确保用户已创建
        ensureTestUserCreated();
        assertNotNull(testStudentId, "测试用户ID不应为空");

        // 创建测试数据
        for (int i = 0; i < 10; i++) {
            LocalDate date = LocalDate.now().minusDays(i);

            // 运动记录
            ExerciseRecordRequest exerciseRequest = new ExerciseRecordRequest();
            exerciseRequest.setExerciseType(i % 2 == 0 ? "跑步" : "游泳");
            exerciseRequest.setDuration(30 + i);
            exerciseRequest.setCaloriesBurned(new BigDecimal(300 + i * 50));
            exerciseRequest.setRecordDate(date);
            exerciseRecordService.addRecord(testStudentId, exerciseRequest);

            // 睡眠记录
            SleepRecordRequest sleepRequest = new SleepRecordRequest();
            sleepRequest.setDuration(new BigDecimal(7.5 + i * 0.1));
            sleepRequest.setQuality(i % 5 + 1);
            sleepRequest.setRecordDate(date);
            sleepRecordService.addRecord(testStudentId, sleepRequest);

            // 心情记录
            MoodRecordRequest moodRequest = new MoodRecordRequest();
            moodRequest.setMoodType(i % 4 + 1);
            moodRequest.setMoodScore(i % 5 + 1);
            moodRequest.setRecordDate(date);
            moodRecordService.addRecord(testStudentId, moodRequest);
        }
        System.out.println("✓ 测试数据准备完成");

        // 测试不同时间范围的统计
        int[] timeRanges = {7, 30};

        for (int days : timeRanges) {
            // 运动统计
            Result exerciseResult = dataAnalysisService.getExerciseStats(testStudentId, days);
            assertEquals(200, exerciseResult.getCode(),
                    days + "天运动统计失败");

            // 睡眠趋势
            Result sleepResult = dataAnalysisService.getSleepTrend(testStudentId, days);
            assertEquals(200, sleepResult.getCode(),
                    days + "天睡眠趋势失败");

            // 心情分布
            Result moodResult = dataAnalysisService.getMoodDistribution(testStudentId, days);
            assertEquals(200, moodResult.getCode(),
                    days + "天心情分布失败");

            System.out.println("✓ " + days + "天统计分析成功");
        }

        System.out.println("多维度数据统计测试通过\n");
    }

    /**
     * 数据一致性验证
     */
    @Test
    @Order(8)
    @DisplayName("数据一致性验证")
    public void testDataConsistency() {
        System.out.println(">>> 测试数据一致性");

        // 确保用户已创建
        ensureTestUserCreated();
        assertNotNull(testStudentId, "测试用户ID不应为空");

        // 1. 添加多条记录
        int recordCount = 10;
        for (int i = 0; i < recordCount; i++) {
            ExerciseRecordRequest request = new ExerciseRecordRequest();
            request.setExerciseType("跑步" + i);
            request.setDuration(30 + i * 5);
            request.setCaloriesBurned(new BigDecimal(String.valueOf(200 + i * 50)));
            request.setRecordDate(LocalDate.now().minusDays(i));

            Result result = exerciseRecordService.addRecord(testStudentId, request);
            assertEquals(200, result.getCode(), "第" + (i + 1) + "条记录创建失败");
        }
        System.out.println("✓ " + recordCount + "条运动记录创建成功");

        // 2. 验证统计数据一致性
        Result statsResult = dataAnalysisService.getExerciseStats(testStudentId, 15);
        assertEquals(200, statsResult.getCode(), "统计数据获取失败");

        // 验证统计数据
        if (statsResult.getData() instanceof Map) {
            Map<String, Object> stats = (Map<String, Object>) statsResult.getData();
            assertNotNull(stats.get("totalDuration"), "总时长不应为空");
            assertNotNull(stats.get("totalCalories"), "总消耗卡路里不应为空");
            assertNotNull(stats.get("avgDuration"), "平均时长不应为空");
        }
        System.out.println("✓ 统计数据一致性验证成功");

        // 3. 验证列表查询结果
        Result listResult = exerciseRecordService.listRecords(testStudentId, null, null, 1, recordCount);
        assertEquals(200, listResult.getCode(), "列表查询失败");

        // 验证分页数据
        if (listResult.getData() instanceof IPage) {
            IPage<?> page = (IPage<?>) listResult.getData();
            assertTrue(page.getTotal() >= recordCount, "记录总数应至少为" + recordCount);
            assertTrue(page.getRecords().size() >= recordCount, "返回记录数应至少为" + recordCount);
        }
        System.out.println("✓ 列表查询一致性验证成功");

        System.out.println("数据一致性验证测试通过\n");
    }

    /**
     * 异常情况处理
     */
    @Test
    @Order(9)
    @DisplayName("异常情况处理")
    public void testExceptionHandling() {
        System.out.println(">>> 测试异常情况处理");

        // 确保用户已创建
        ensureTestUserCreated();
        assertNotNull(testStudentId, "测试用户ID不应为空");

        // 1. 测试无效的数据（负卡路里）
        DietRecordRequest invalidRequest = new DietRecordRequest();
        invalidRequest.setMealType(1);
        invalidRequest.setCalories(new BigDecimal("-100")); // 无效值
        invalidRequest.setRecordDate(LocalDate.now());

        Result invalidResult = dietRecordService.addRecord(testStudentId, invalidRequest);
        assertNotEquals(200, invalidResult.getCode(), "应拒绝无效数据");
        System.out.println("✓ 无效数据验证成功（正确拒绝）");

        // 2. 测试空值
        DietRecordRequest nullRequest = new DietRecordRequest();
        // 不设置必要字段
        Result nullResult = dietRecordService.addRecord(testStudentId, nullRequest);
        assertNotEquals(200, nullResult.getCode(), "应拒绝空数据");
        System.out.println("✓ 空数据验证成功（正确拒绝）");

        // 3. 测试不存在的用户ID
        Result nonExistUserResult = dietRecordService.addRecord(-999L, invalidRequest);
        assertNotEquals(200, nonExistUserResult.getCode(), "应处理不存在的用户ID");
        System.out.println("✓ 不存在用户ID验证成功");

        // 4. 测试超出范围的值
        SleepRecordRequest outOfRangeRequest = new SleepRecordRequest();
        outOfRangeRequest.setDuration(new BigDecimal("25.0")); // 超过24小时
        outOfRangeRequest.setQuality(6); // 超过最大质量值
        outOfRangeRequest.setRecordDate(LocalDate.now());

        Result outOfRangeResult = sleepRecordService.addRecord(testStudentId, outOfRangeRequest);
        assertNotEquals(200, outOfRangeResult.getCode(), "应拒绝超出范围的值");
        System.out.println("✓ 超出范围值验证成功");

        System.out.println("异常情况处理测试通过\n");
    }

    /**
     * 性能测试 - 批量数据处理（默认禁用）
     */
    @Test
    @Order(10)
    @DisplayName("性能测试 - 批量数据处理")
    @Disabled("性能测试可根据需要启用") // 可根据需要启用/禁用
    public void testPerformance() {
        System.out.println(">>> 测试批量数据处理性能");

        // 确保用户已创建
        ensureTestUserCreated();
        assertNotNull(testStudentId, "测试用户ID不应为空");

        int batchSize = 50;
        long startTime = System.currentTimeMillis();

        // 批量创建记录
        for (int i = 0; i < batchSize; i++) {
            ExerciseRecordRequest request = new ExerciseRecordRequest();
            request.setExerciseType("运动" + i);
            request.setDuration(30);
            request.setCaloriesBurned(new BigDecimal("300"));
            request.setRecordDate(LocalDate.now().minusDays(i % 30));

            Result result = exerciseRecordService.addRecord(testStudentId, request);
            assertEquals(200, result.getCode(), "批量创建第" + i + "条记录失败");
        }

        long endTime = System.currentTimeMillis();
        long createDuration = endTime - startTime;

        System.out.println("✓ " + batchSize + "条记录创建完成，耗时: " + createDuration + "ms");
        assertTrue(createDuration < 10000, "批量创建应在10秒内完成（实际耗时：" + createDuration + "ms）");

        // 测试批量查询性能
        startTime = System.currentTimeMillis();
        Result listResult = exerciseRecordService.listRecords(testStudentId, null, null, 1, batchSize);
        endTime = System.currentTimeMillis();
        long queryDuration = endTime - startTime;

        assertEquals(200, listResult.getCode(), "批量查询失败");
        System.out.println("✓ 批量查询完成，耗时: " + queryDuration + "ms");
        assertTrue(queryDuration < 2000, "批量查询应在2秒内完成（实际耗时：" + queryDuration + "ms）");

        System.out.println("✅ 性能测试通过\n");
    }

    /**
     * 确保测试用户已创建（兜底方法）
     */
    private void ensureTestUserCreated() {
        if (testStudentId == null || !isTestUserExists(TEST_STUDENT_NO)) {
            // 手动注册用户
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setRole(TEST_ROLE);
            registerRequest.setStudentNo(TEST_STUDENT_NO);
            registerRequest.setPassword(TEST_PASSWORD);
            registerRequest.setName("兜底测试用户");
            registerRequest.setGender(1);
            registerRequest.setPhone("13900139000");
            registerRequest.setEmail("兜底@test.com");
            registerRequest.setMajor("计算机");
            registerRequest.setClassName("CS2024");

            Result registerResult = authService.register(registerRequest);
            if (registerResult.getCode() == 200) {
                isTestUserExists(TEST_STUDENT_NO); // 更新testStudentId
                System.out.println("⚠️ 兜底创建测试用户成功");
            } else {
                fail("兜底创建用户失败：" + registerResult.getMessage());
            }
        }
    }

    /**
     * 准备测试健康数据
     */
    private void prepareTestHealthData(Long studentId) {
        // 体检记录
        HealthExaminationRequest examRequest = new HealthExaminationRequest();
        examRequest.setHeight(new BigDecimal("175.0"));
        examRequest.setWeight(new BigDecimal("70.0"));
        examRequest.setBloodPressureHigh(120);
        examRequest.setBloodPressureLow(80);
        examRequest.setHeartRate(72);
        examRequest.setExamDate(LocalDate.now());
        healthExaminationService.addExamination(studentId, examRequest);

        // 运动记录
        ExerciseRecordRequest exerciseRequest = new ExerciseRecordRequest();
        exerciseRequest.setExerciseType("跑步");
        exerciseRequest.setDuration(30);
        exerciseRequest.setCaloriesBurned(new BigDecimal("300"));
        exerciseRequest.setRecordDate(LocalDate.now());
        exerciseRecordService.addRecord(studentId, exerciseRequest);

        // 睡眠记录
        SleepRecordRequest sleepRequest = new SleepRecordRequest();
        sleepRequest.setDuration(new BigDecimal("8.0"));
        sleepRequest.setQuality(4);
        sleepRequest.setRecordDate(LocalDate.now());
        sleepRecordService.addRecord(studentId, sleepRequest);

        // 心情记录
        MoodRecordRequest moodRequest = new MoodRecordRequest();
        moodRequest.setMoodType(1);
        moodRequest.setMoodScore(5);
        moodRequest.setRecordDate(LocalDate.now());
        moodRecordService.addRecord(studentId, moodRequest);

        // 饮食记录
        DietRecordRequest dietRequest = new DietRecordRequest();
        dietRequest.setMealType(2);
        dietRequest.setFoodName("健康餐");
        dietRequest.setCalories(new BigDecimal("600"));
        dietRequest.setProtein(new BigDecimal("30.0"));
        dietRequest.setFat(new BigDecimal("20.0"));
        dietRequest.setCarbs(new BigDecimal("80.0"));
        dietRequest.setRecordDate(LocalDate.now());
        dietRecordService.addRecord(studentId, dietRequest);
    }

    /**
     * 记录ID获取方法
     */
    private Long getCreatedRecordId(Long studentId) {
        // 1. 前置校验：避免空指针
        if (studentId == null || studentId <= 0) {
            System.err.println("获取记录ID失败：学生ID无效，studentId=" + studentId);
            return null;
        }

        // 2. 先尝试接口查询
        Result listResult = exerciseRecordService.listRecords(studentId, null, null, 1, 10);
        if (listResult != null && listResult.getCode() == 200) {
            Object data = listResult.getData();
            List<?> records = null;

            if (data instanceof IPage) {
                IPage<?> page = (IPage<?>) data;
                records = page.getRecords();
            } else if (data instanceof Map) {
                Map<String, Object> dataMap = (Map<String, Object>) data;
                Object recordsObj = dataMap.get("records");
                if (recordsObj instanceof List) {
                    records = (List<?>) recordsObj;
                }
            }

            if (records != null && !records.isEmpty()) {
                Object latestRecord = records.get(0);
                Long recordId = getRecordIdFromObject(latestRecord);
                if (recordId != null) {
                    return recordId;
                }
            }
        }

        // 3. 兜底：直接查询数据库（绕过Service，避免过滤）
        try {
            // 查询该学生最新的运动记录
            ExerciseRecord record = exerciseRecordMapper.selectOne(
                    new QueryWrapper<ExerciseRecord>()
                            .eq("student_id", studentId)
                            .orderByDesc("create_time")
                            .last("LIMIT 1")
            );
            if (record != null) {
                return record.getId();
            }
        } catch (Exception e) {
            System.err.println("数据库直查获取ID失败：" + e.getMessage());
        }

        System.err.println("所有方式都无法获取记录ID");
        return null;
    }

    /**
     * 从任意对象中提取ID
     */
    private Long getRecordIdFromObject(Object record) {
        if (record == null) {
            return null;
        }

        // 方式1：实体类 getId() 方法
        Optional<Method> getIdMethodOpt = ReflectionUtils.findMethod(record.getClass(), "getId");
        if (getIdMethodOpt.isPresent()) {
            try {
                Object idObj = getIdMethodOpt.get().invoke(record);
                if (idObj instanceof Number) {
                    return ((Number) idObj).longValue();
                }
            } catch (Exception e) {
                // 忽略异常，尝试下一种方式
            }
        }

        // 方式2：实体类 getRecordId() 方法
        Optional<Method> getRecordIdMethodOpt = ReflectionUtils.findMethod(record.getClass(), "getRecordId");
        if (getRecordIdMethodOpt.isPresent()) {
            try {
                Object idObj = getRecordIdMethodOpt.get().invoke(record);
                if (idObj instanceof Number) {
                    return ((Number) idObj).longValue();
                }
            } catch (Exception e) {
                // 忽略异常，尝试下一种方式
            }
        }

        // 方式3：Map 类型
        if (record instanceof Map) {
            Map<String, Object> recordMap = (Map<String, Object>) record;
            // 兼容 id/recordId/record_id 等字段名
            Object idObj = recordMap.get("id");
            if (idObj == null) idObj = recordMap.get("recordId");
            if (idObj == null) idObj = recordMap.get("record_id");

            if (idObj instanceof Number) {
                return ((Number) idObj).longValue();
            }
        }

        return null;
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("\n=================================");
        System.out.println("✅ 所有集成测试完成！");
        System.out.println("=================================");
    }
}

