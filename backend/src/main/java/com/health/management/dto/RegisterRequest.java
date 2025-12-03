package com.health.management.dto;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * 注册请求DTO
 */
@Data
public class RegisterRequest {
    
    /**
     * 用户角色: STUDENT-学生, ADMIN-管理员
     */
    @NotBlank(message = "角色不能为空")
    private String role;
    
    /**
     * 学号（学生注册时使用）
     */
    private String studentNo;
    
    /**
     * 工号（管理员注册时使用）
     */
    private String employeeNo;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码至少6位")
    private String password;
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotNull(message = "性别不能为空")
    private Integer gender;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 专业（学生注册时使用）
     */
    private String major;
    
    /**
     * 班级（学生注册时使用）
     */
    private String className;
    
    /**
     * 部门（管理员注册时使用）
     */
    private String department;
}
