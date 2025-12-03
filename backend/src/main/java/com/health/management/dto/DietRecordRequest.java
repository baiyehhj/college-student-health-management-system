package com.health.management.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 饮食记录请求DTO
 */
@Data
public class DietRecordRequest {
    
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;
    
    @NotNull(message = "餐次不能为空")
    private Integer mealType;
    
    @NotBlank(message = "食物名称不能为空")
    private String foodName;
    
    private String foodCategory;
    
    private BigDecimal calories;
    
    private BigDecimal protein;
    
    private BigDecimal carbs;
    
    private BigDecimal fat;
    
    private String description;
}
