package com.health.management.integration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.management.BaseTest;
import com.health.management.dto.DietRecordRequest;
import com.health.management.dto.ExerciseRecordRequest;
import com.health.management.dto.SleepRecordRequest;
import com.health.management.entity.DietRecord;
import com.health.management.entity.ExerciseRecord;
import com.health.management.entity.SleepRecord;
import com.health.management.entity.StudentUser;
import com.health.management.mapper.DietRecordMapper;
import com.health.management.mapper.ExerciseRecordMapper;
import com.health.management.mapper.SleepRecordMapper;
import com.health.management.mapper.StudentUserMapper;
import com.health.management.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 健康记录集成测试类
 * 测试完整的健康记录增删改查流程
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("健康记录集成测试")
public class HealthRecordIntegrationTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private DietRecordMapper dietRecordMapper;

    @Autowired
    private ExerciseRecordMapper exerciseRecordMapper;

    @Autowired
    private SleepRecordMapper sleepRecordMapper;

    @Autowired
    private StudentUserMapper studentUserMapper; // 新增学生Mapper

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private String testToken;
    private Long createdDietRecordId;
    private Long createdExerciseRecordId;
    private Long createdSleepRecordId;
    private Long testStudentId; // 存储测试学生的真实ID
    private Long otherStudentId;
    private static final String TEST_STUDENT_NO = "20230001"; // 常量化学生编号
    private static final String OTHER_STUDENT_NO = "20230002";

    @BeforeEach
    public void setUp() {
        // 1. 先删除已存在的测试学生（避免唯一键冲突）
        jdbcTemplate.update("DELETE FROM student_user WHERE student_no IN (?, ?)", TEST_STUDENT_NO, OTHER_STUDENT_NO);

        // 2. 插入测试学生
        String insertStudentSql =
                "INSERT INTO student_user (student_no, password, name, gender, age, phone, email, major, class_name) " +
                        "VALUES (?, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '测试学生', 1, 20, '13800138001', 'test@student.com', '计算机', '计科2301班')";
        jdbcTemplate.update(insertStudentSql, TEST_STUDENT_NO);

        // 3. 插入另一个测试学生（用于无权限测试）
        String insertOtherStudentSql =
                "INSERT INTO student_user (student_no, password, name, gender, age, phone, email, major, class_name) " +
                        "VALUES (?, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '其他学生', 1, 21, '13800138002', 'other@student.com', '软件工程', '软工2301班')";
        jdbcTemplate.update(insertOtherStudentSql, OTHER_STUDENT_NO);

        // 4. 查询主测试学生ID
        LambdaQueryWrapper<StudentUser> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(StudentUser::getStudentNo, TEST_STUDENT_NO);
        StudentUser testStudent = studentUserMapper.selectOne(lambdaQuery);
        if (testStudent == null) {
            throw new RuntimeException("测试学生插入失败，无法获取学生ID");
        }
        testStudentId = testStudent.getId(); // 保存真实学生ID

        // 新增查询另一个学生ID的逻辑
        LambdaQueryWrapper<StudentUser> otherLambdaQuery = new LambdaQueryWrapper<>();
        otherLambdaQuery.eq(StudentUser::getStudentNo, OTHER_STUDENT_NO);
        StudentUser otherStudent = studentUserMapper.selectOne(otherLambdaQuery);
        if (otherStudent == null) {
            throw new RuntimeException("其他测试学生插入失败，无法获取学生ID");
        }
        otherStudentId = otherStudent.getId();

        // 5. 设置JWT配置
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_JWT_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", TEST_JWT_EXPIRATION);

        // 6. 生成测试token（使用真实学生ID）
        testToken = "Bearer " + jwtUtil.generateToken(testStudentId, TEST_STUDENT_NO);
    }

    @AfterEach
    public void tearDown() {
        // 1. 清理健康记录数据
        if (createdDietRecordId != null) {
            dietRecordMapper.deleteById(createdDietRecordId);
        }
        if (createdExerciseRecordId != null) {
            exerciseRecordMapper.deleteById(createdExerciseRecordId);
        }
        if (createdSleepRecordId != null) {
            sleepRecordMapper.deleteById(createdSleepRecordId);
        }

        // 2. 清理测试学生（关键：同时删除两个测试学生）
        jdbcTemplate.update("DELETE FROM student_user WHERE student_no IN (?, ?)", TEST_STUDENT_NO, OTHER_STUDENT_NO);

        // 3. 重置变量
        testStudentId = null;
        otherStudentId = null;
        createdDietRecordId = null;
        createdExerciseRecordId = null;
        createdSleepRecordId = null;
    }

    @Test
    @DisplayName("饮食记录完整流程 - 添加、查询、更新、删除")
    public void testDietRecordCompleteFlow() throws Exception {
        // 1. 添加饮食记录
        DietRecordRequest addRequest = new DietRecordRequest();
        addRequest.setRecordDate(LocalDate.now());
        addRequest.setMealType(1);
        addRequest.setFoodName("牛奶面包");
        addRequest.setFoodCategory("主食");
        addRequest.setCalories(new BigDecimal("300.0"));
        addRequest.setProtein(new BigDecimal("12.0"));
        addRequest.setCarbs(new BigDecimal("50.0"));
        addRequest.setFat(new BigDecimal("8.0"));

        MvcResult addResult = mockMvc.perform(post("/diet/add")
                        .header("Authorization", testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("添加结果: " + addResult.getResponse().getContentAsString());
        createdDietRecordId = extractRecordIdFromResponse(addResult.getResponse().getContentAsString());

        // 2. 查询饮食记录列表
        mockMvc.perform(get("/diet/list")
                        .header("Authorization", testToken)
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .param("endDate", LocalDate.now().plusDays(1).toString())
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());

        // 3. 更新饮食记录
        if (createdDietRecordId != null) {
            DietRecordRequest updateRequest = new DietRecordRequest();
            updateRequest.setRecordDate(LocalDate.now());
            updateRequest.setMealType(2);
            updateRequest.setFoodName("米饭炒菜");
            updateRequest.setFoodCategory("主食");
            updateRequest.setCalories(new BigDecimal("500.0"));
            updateRequest.setProtein(new BigDecimal("20.0"));
            updateRequest.setCarbs(new BigDecimal("80.0"));
            updateRequest.setFat(new BigDecimal("15.0"));

            mockMvc.perform(put("/diet/update/" + createdDietRecordId)
                            .header("Authorization", testToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            // 验证更新
            DietRecord updated = dietRecordMapper.selectById(createdDietRecordId);
            assertNotNull(updated);
            assertEquals("米饭炒菜", updated.getFoodName());
            assertEquals(2, updated.getMealType());
        }

        // 4. 获取每日统计
        mockMvc.perform(get("/diet/stats/daily")
                        .header("Authorization", testToken)
                        .param("date", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 5. 删除饮食记录
        if (createdDietRecordId != null) {
            mockMvc.perform(delete("/diet/delete/" + createdDietRecordId)
                            .header("Authorization", testToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            // 验证删除
            DietRecord deleted = dietRecordMapper.selectById(createdDietRecordId);
            assertNull(deleted, "记录应该被删除");
            createdDietRecordId = null;
        }
    }

    @Test
    @DisplayName("运动记录完整流程 - 添加、查询、统计")
    public void testExerciseRecordCompleteFlow() throws Exception {
        // 1. 添加运动记录
        ExerciseRecordRequest addRequest = new ExerciseRecordRequest();
        addRequest.setRecordDate(LocalDate.now());
        addRequest.setExerciseType("跑步");
        addRequest.setDuration(30);
        addRequest.setCaloriesBurned(new BigDecimal("200.0"));


        MvcResult addResult = mockMvc.perform(post("/exercise/add")
                        .header("Authorization", testToken) // Token中已包含学生ID，后端应从Token解析
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("添加运动记录: " + addResult.getResponse().getContentAsString());
        createdExerciseRecordId = extractRecordIdFromResponse(addResult.getResponse().getContentAsString());

        // 2. 查询运动记录列表
        mockMvc.perform(get("/exercise/list")
                        .header("Authorization", testToken)
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .param("endDate", LocalDate.now().plusDays(1).toString())
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 3. 获取运动统计
        mockMvc.perform(get("/analysis/exercise-stats")
                        .header("Authorization", testToken)
                        .param("startDate", LocalDate.now().minusDays(7).toString())
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("睡眠记录完整流程 - 添加、查询、趋势")
    public void testSleepRecordCompleteFlow() throws Exception {
        // 1. 添加睡眠记录
        SleepRecordRequest addRequest = new SleepRecordRequest();
        addRequest.setRecordDate(LocalDate.now());
        addRequest.setSleepTime(LocalDateTime.now().minusHours(8));
        addRequest.setWakeTime(LocalDateTime.now());
        addRequest.setDuration(new BigDecimal("8.5"));
        addRequest.setQuality(3);

        MvcResult addResult = mockMvc.perform(post("/sleep/add")
                        .header("Authorization", testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("添加睡眠记录: " + addResult.getResponse().getContentAsString());
        createdSleepRecordId = extractRecordIdFromResponse(addResult.getResponse().getContentAsString());

        // 2. 查询睡眠记录列表
        mockMvc.perform(get("/sleep/list")
                        .header("Authorization", testToken)
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .param("endDate", LocalDate.now().plusDays(1).toString())
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 3. 获取睡眠趋势
        mockMvc.perform(get("/analysis/sleep-trend")
                        .header("Authorization", testToken)
                        .param("startDate", LocalDate.now().minusDays(7).toString())
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("无权限操作他人记录 - 应该失败")
    public void testUnauthorizedAccess() throws Exception {
        // 1. 验证 otherStudentId 不为空（避免空指针）
        assertNotNull(otherStudentId, "其他学生ID获取失败");

        // 2. 创建另一个学生的饮食记录（显式设置 studentId）
        DietRecord otherUserRecord = new DietRecord();
        otherUserRecord.setStudentId(otherStudentId); // 确保调用 setStudentId 方法
        otherUserRecord.setRecordDate(LocalDate.now());
        otherUserRecord.setMealType(1);
        otherUserRecord.setFoodName("测试食物");
        otherUserRecord.setCreateTime(LocalDateTime.now());

        // 3. 插入记录
        dietRecordMapper.insert(otherUserRecord);
        Long otherRecordId = otherUserRecord.getId();

        try {
            // 尝试更新他人记录
            DietRecordRequest updateRequest = new DietRecordRequest();
            updateRequest.setRecordDate(LocalDate.now());
            updateRequest.setMealType(1);
            updateRequest.setFoodName("恶意更新");

            mockMvc.perform(put("/diet/update/" + otherRecordId)
                            .header("Authorization", testToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("无权限操作"));

            // 尝试删除他人记录
            mockMvc.perform(delete("/diet/delete/" + otherRecordId)
                            .header("Authorization", testToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("无权限操作"));

        } finally {
            // 清理测试数据
            if (otherRecordId != null) {
                dietRecordMapper.deleteById(otherRecordId);
            }
        }
    }

    /**
     * 从响应中提取记录ID
     */
    private Long extractRecordIdFromResponse(String responseBody) {
        try {
            if (responseBody.contains("\"id\"")) {
                int idIndex = responseBody.indexOf("\"id\"");
                String idStr = responseBody.substring(idIndex);
                int start = idStr.indexOf(":") + 1;
                int end = idStr.indexOf(",", start);
                if (end == -1) {
                    end = idStr.indexOf("}", start);
                }
                String idValue = idStr.substring(start, end).trim();
                return Long.parseLong(idValue);
            }
        } catch (Exception e) {
            System.err.println("提取ID失败: " + e.getMessage());
        }
        return null;
    }
}
