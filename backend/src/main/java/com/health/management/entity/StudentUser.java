package com.health.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学生用户实体类
 */
@Data
@TableName("student_user")
public class StudentUser {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String studentNo;
    
    private String password;
    
    private String name;
    
    private Integer gender;  // 1男 2女
    
    private Integer age;
    
    private String phone;
    
    private String email;
    
    private String major;
    
    private String className;
    
    private String avatar;
    
    private Integer status;  // 1正常 0禁用
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
