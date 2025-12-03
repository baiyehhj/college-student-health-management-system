package com.health.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统通知实体类
 */
@Data
@TableName("system_notification")
public class SystemNotification {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private String title;
    
    private String content;
    
    private Integer type;
    
    private Integer priority;
    
    private Integer isRead;
    
    private LocalDateTime readTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
