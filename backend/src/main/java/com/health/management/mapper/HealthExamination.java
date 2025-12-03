package com.health.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体检报告实体类
 */
@Data
@TableName("health_examination")
public class HealthExamination {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private LocalDate examDate;
    
    private String examType;
    
    private BigDecimal height;
    
    private BigDecimal weight;
    
    private BigDecimal bmi;
    
    private Integer bloodPressureHigh;
    
    private Integer bloodPressureLow;
    
    private Integer heartRate;
    
    private BigDecimal visionLeft;
    
    private BigDecimal visionRight;
    
    private BigDecimal bloodSugar;
    
    private BigDecimal hemoglobin;
    
    private BigDecimal whiteBloodCell;
    
    private BigDecimal platelet;
    
    private String reportFile;
    
    private String overallConclusion;
    
    private String doctorAdvice;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
