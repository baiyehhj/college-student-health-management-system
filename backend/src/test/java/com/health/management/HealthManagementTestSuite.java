package com.health.management;

import com.health.management.controller.AuthControllerTest;
import com.health.management.controller.DietRecordControllerTest;
import com.health.management.integration.AuthenticationIntegrationTest;
import com.health.management.integration.HealthRecordIntegrationTest;
import com.health.management.service.*;
import com.health.management.util.JwtUtilTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;


/**
 * 测试套件
 * 组织和运行所有测试类
 */
@Suite
@SuiteDisplayName("大学生健康管理系统测试套件")
@SelectClasses({
    // 工具类测试
    JwtUtilTest.class,
    
    // 服务层测试
    AuthServiceTest.class,
    DietRecordServiceTest.class,
    ExerciseRecordServiceTest.class,
    SleepRecordServiceTest.class,
    MoodRecordServiceTest.class,
    UserServiceTest.class,
    HealthExaminationServiceTest.class,
    
    // 控制器层测试
    AuthControllerTest.class,
    DietRecordControllerTest.class,
    
    // 集成测试
    AuthenticationIntegrationTest.class,
    HealthRecordIntegrationTest.class
})
class HealthManagementTestSuite {
    // 测试套件类，用于组织所有测试
}
