package com.health.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 运动记录实体类
 */
@Data
@TableName("exercise_record")
public class ExerciseRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private LocalDate recordDate;
    
    private String exerciseType;
    
    private Integer duration;
    
    private BigDecimal caloriesBurned;
    
    private Integer intensity;
    
    private BigDecimal distance;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
