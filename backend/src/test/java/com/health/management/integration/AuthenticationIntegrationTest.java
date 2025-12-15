package com.health.management.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.management.dto.LoginRequest;
import com.health.management.dto.RegisterRequest;
import com.health.management.mapper.StudentUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentUserMapper studentUserMapper;

    private static final String TEST_STUDENT_NO = "2021999";
    private static final String TEST_PASSWORD = "123456";
    private static final String TEST_NAME = "集成测试学生";
    private static final String TEST_PHONE = "13800138000";
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        // 初始化 MockMvc 并禁用 CSRF
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)

                .build();
    }


    /**
     * 创建有效的注册请求
     */
    private RegisterRequest createValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setRole("STUDENT");
        request.setStudentNo(TEST_STUDENT_NO);
        request.setPassword(TEST_PASSWORD);
        request.setName(TEST_NAME);
        request.setGender(1);
        request.setPhone(TEST_PHONE);
        request.setEmail("test@example.com");
        request.setMajor("计算机科学与技术");
        request.setClassName("2021级1班");
        return request;
    }

    /**
     * 测试完整的认证流程：注册 -> 登录 -> 使用token访问
     */
    @Test
    public void testCompleteAuthenticationFlow() throws Exception {
        // 1. 注册用户
        RegisterRequest registerRequest = createValidRegisterRequest();
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));

        // 2. 登录获取token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setRole("STUDENT");
        loginRequest.setStudentNo(TEST_STUDENT_NO);
        loginRequest.setPassword(TEST_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.userInfo.name").value(TEST_NAME))
                .andReturn();

        // 验证token存在
        String responseBody = loginResult.getResponse().getContentAsString();
        assert responseBody.contains("token");
    }

    /**
     * 测试重复注册
     */
    @Test
    public void testDuplicateRegistration() throws Exception {
        // 第一次注册（有效参数）
        RegisterRequest registerRequest = createValidRegisterRequest();
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 第二次注册相同学号
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("该学号已被注册"));
    }

    /**
     * 测试使用错误密码登录
     */
    @Test
    public void testLoginWithWrongPassword() throws Exception {
        // 先注册用户
        RegisterRequest registerRequest = createValidRegisterRequest();
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 使用错误密码登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setRole("STUDENT");
        loginRequest.setStudentNo(TEST_STUDENT_NO);
        loginRequest.setPassword("wrong_password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("学号或密码错误"));
    }

    /**
     * 测试使用不存在的账号登录
     */
    @Test
    public void testLoginWithNonExistentAccount() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setRole("STUDENT");
        loginRequest.setStudentNo("9999999");
        loginRequest.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("学号或密码错误"));
    }

}
