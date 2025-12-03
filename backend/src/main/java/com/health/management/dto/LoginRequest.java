package com.health.management.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 */
@Data
public class LoginRequest {
    
    /**
     * 用户角色: STUDENT-学生, ADMIN-管理员
     */
    @NotBlank(message = "角色不能为空")
    private String role;
    
    /**
     * 学号（学生登录时使用）
     */
    private String studentNo;
    
    /**
     * 工号（管理员登录时使用）
     */
    private String employeeNo;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}
