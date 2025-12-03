package com.health.management;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot主应用类
 */
@SpringBootApplication
@MapperScan("com.health.management.mapper")
public class HealthManagementApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(HealthManagementApplication.class, args);
        System.out.println("=================================");
        System.out.println("大学生健康管理系统启动成功!");
        System.out.println("访问地址: http://localhost:8080");
        System.out.println("=================================");
    }
}
