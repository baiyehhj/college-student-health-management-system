package com.health.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 情绪记录实体类
 */
@Data
@TableName("mood_record")
public class MoodRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private LocalDate recordDate;
    
    private LocalDateTime recordTime;
    
    private Integer moodType;
    
    private Integer moodScore;
    
    private String triggerEvent;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
