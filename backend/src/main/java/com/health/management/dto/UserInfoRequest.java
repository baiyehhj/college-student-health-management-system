package com.health.management.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * 用户信息更新请求DTO
 */
@Data
public class UserInfoRequest {
    
    @Size(min = 2, max = 50, message = "姓名长度应在2-50个字符之间")
    private String name;
    
    @Min(value = 1, message = "性别：1-男，2-女")
    @Max(value = 2, message = "性别：1-男，2-女")
    private Integer gender;
    
    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 150, message = "年龄必须小于150")
    private Integer age;
    
    @Size(max = 20, message = "电话号码长度不能超过20")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;
    
    @Size(max = 100, message = "专业名称长度不能超过100")
    private String major;
    
    @Size(max = 50, message = "班级名称长度不能超过50")
    private String className;
    
    @Size(max = 255, message = "头像URL长度不能超过255")
    private String avatar;
}
