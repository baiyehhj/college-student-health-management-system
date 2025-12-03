package com.health.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员工号预设表实体类（用于验证注册时的工号是否有效）
 */
@Data
@TableName("admin_employee_list")
public class AdminEmployeeList {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String employeeNo;
    
    private String name;
    
    private String department;
    
    private Integer isRegistered;  // 0未注册 1已注册
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
