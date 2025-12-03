package com.health.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 睡眠记录实体类
 */
@Data
@TableName("sleep_record")
public class SleepRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private LocalDate recordDate;
    
    private LocalDateTime sleepTime;
    
    private LocalDateTime wakeTime;
    
    private BigDecimal duration;
    
    private Integer quality;
    
    private BigDecimal deepSleepDuration;
    
    private Integer dreamCount;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
