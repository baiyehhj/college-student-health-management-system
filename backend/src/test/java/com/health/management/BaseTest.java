package com.health.management;

import com.health.management.config.PasswordEncoderConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 测试基类
 * 所有测试类继承此类以获得Spring环境支持
 * 自动适配 @WebMvcTest 和 @SpringBootTest 场景，避免注解冲突
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HealthManagementApplication.class)
@ActiveProfiles("test")
@TestExecutionListeners(listeners = {
        BaseTest.WebMvcTestDetectionListener.class,
        DirtiesContextTestExecutionListener.class // 引入脏上下文监听器
}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class BaseTest {

    /**
     * 测试用户ID
     */
    protected static final Long TEST_STUDENT_ID = 1L;

    /**
     * 测试管理员ID
     */
    protected static final Long TEST_ADMIN_ID = 1L;

    /**
     * 测试学号
     */
    protected static final String TEST_STUDENT_NO = "2021001";

    /**
     * 测试工号
     */
    protected static final String TEST_EMPLOYEE_NO = "E001";

    /**
     * 测试密码
     */
    protected static final PasswordEncoderConfig.PasswordEncoder encoder = new PasswordEncoderConfig.PasswordEncoder();
    protected static final String TEST_PASSWORD = encoder.encode("123456");

    /**
     * 测试JWT Secret
     */
    protected static final String TEST_JWT_SECRET = "health_management_system_secret_key_2025";

    /**
     * 测试JWT过期时间（毫秒）
     */
    protected static final Long TEST_JWT_EXPIRATION = 86400000L; // 24小时

    /**
     * 自定义监听器：检测测试类是否有 @WebMvcTest，有则跳过 SpringBootTest 上下文加载
     * 完全兼容所有 Spring 版本，无语法错误
     */
    public static class WebMvcTestDetectionListener implements TestExecutionListener {

        // Spring 上下文属性名常量（替换废弃/不存在的 APPLICATION_CONTEXT_ATTRIBUTE_NAME）
        private static final String APPLICATION_CONTEXT_ATTRIBUTE_NAME =
                "org.springframework.test.context.TestContext.APPLICATION_CONTEXT";

        @Override
        public void beforeTestClass(TestContext testContext) throws Exception {
            // 获取当前测试类
            Class<?> testClass = testContext.getTestClass();
            if (testClass == null) {
                return;
            }

            // 检查测试类是否有 @WebMvcTest 注解（包括父类/接口）
            WebMvcTest webMvcTest = AnnotationUtils.findAnnotation(testClass, WebMvcTest.class);
            if (webMvcTest != null) {
                // 1. 标记为 WebMvcTest 场景
                testContext.setAttribute("IS_WEB_MVC_TEST", Boolean.TRUE);

                // 2. 安全关闭 SpringBootTest 上下文（修复 markApplicationContextDirty 参数问题）
                if (testContext.hasApplicationContext()) {
                    ConfigurableApplicationContext ctx =
                            (ConfigurableApplicationContext) testContext.getApplicationContext();
                    if (ctx.isActive()) {
                        ctx.close(); // 关闭上下文
                    }
                    // 修复：添加 HierarchyMode 参数（Spring 要求必填）
                    testContext.markApplicationContextDirty(
                            org.springframework.test.annotation.DirtiesContext.HierarchyMode.CURRENT_LEVEL
                    );
                }

                // 3. 清空上下文属性，阻止冲突
                testContext.removeAttribute(APPLICATION_CONTEXT_ATTRIBUTE_NAME);
            }
        }

        @Override
        public void prepareTestInstance(TestContext testContext) throws Exception {
            // WebMvcTest 场景下，直接返回空上下文，避免 SpringBootTest 加载
            if (Boolean.TRUE.equals(testContext.getAttribute("IS_WEB_MVC_TEST"))) {
                testContext.setAttribute(APPLICATION_CONTEXT_ATTRIBUTE_NAME, null);
            }
        }
    }

    /**
     * 可选：自定义注解，标记不需要 SpringBootTest 的测试类
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DisableSpringBootTest {
    }
}
