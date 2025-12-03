package com.health.management.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 用户信息更新请求DTO
 */
@Data
public class UserProfileRequest {
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    private Integer gender; // 1:男, 2:女
    
    private Integer age;
    
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String major;
    
    private String className;
}
