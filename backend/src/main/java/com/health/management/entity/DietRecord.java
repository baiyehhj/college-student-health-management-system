package com.health.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 饮食记录实体类
 */
@Data
@TableName("diet_record")
public class DietRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private LocalDate recordDate;
    
    private Integer mealType;  // 1早餐 2午餐 3晚餐 4加餐
    
    private String foodName;
    
    private String foodCategory;
    
    private BigDecimal calories;
    
    private BigDecimal protein;
    
    private BigDecimal carbs;
    
    private BigDecimal fat;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
