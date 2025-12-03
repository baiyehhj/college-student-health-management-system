package com.health.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员用户实体类
 */
@Data
@TableName("admin_user")
public class AdminUser {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String employeeNo;
    
    private String password;
    
    private String name;
    
    private Integer gender;  // 1男 2女
    
    private String phone;
    
    private String email;
    
    private String department;
    
    private String avatar;
    
    private Integer status;  // 1正常 0禁用
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
