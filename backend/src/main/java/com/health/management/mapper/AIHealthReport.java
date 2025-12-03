package com.health.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AI健康报告实体类
 */
@Data
@TableName("ai_health_report")
public class AIHealthReport {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private LocalDate reportDate;
    
    private String analysisPeriod;
    
    private BigDecimal overallScore;
    
    private String dietAnalysis;
    
    private String exerciseAnalysis;
    
    private String sleepAnalysis;
    
    private String moodAnalysis;
    
    private String healthRisks;
    
    private String recommendations;
    
    private String aiModelVersion;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
